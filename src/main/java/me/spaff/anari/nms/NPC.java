package me.spaff.anari.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import me.spaff.anari.utils.ReflectionUtils;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R3.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {
    private final ServerPlayer npc;
    private Location location;

    private final GameProfile profile;
    private final int entityId;
    private final List<Pair<EquipmentSlot, ItemStack>> equipment;

    public NPC(Builder builder) {
        this.npc = builder.npc;
        this.location = builder.location;
        this.profile = builder.profile;
        this.entityId = builder.entityId;
        this.equipment = builder.equipment;
    }

    // Show
    public void show(Player player) {
        Packets.sendPlayerInfoPacket(player, npc, ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        Packets.sendAddEntityPacket(player, npc, location, entityId);
        Packets.sendTeamAddPacket(player, profile.getName()); // Disable nametag
        if (!equipment.isEmpty())
            Packets.sendEquipmentPacket(player, entityId, equipment);
    }

    public void show() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            show(player);
        }
    }

    // Remove
    public void remove(Player player) {
        Packets.sendRemoveEntityPacket(player, npc);
    }

    public void remove() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            remove(player);
        }
    }

    // Teleport
    public void teleport(Player player, Location location) {
        this.location = location;
        Packets.sendTeleportPacket(player, npc, location);
    }

    public void teleport(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            teleport(player, location);
        }
    }

    // Animation

    public void playAnimation(Player player, NPCAction action) {
        Packets.sendAnimatePacket(player, npc, action);
    }

    public void playAnimation(NPCAction action) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playAnimation(player, action);
        }
    }

    // Getters

    public GameProfile getProfile() {
        return profile;
    }

    public static class Builder {
        private final ServerPlayer npc;
        private final Location location;

        private final GameProfile profile;
        private int entityId;

        private final List<Pair<EquipmentSlot, ItemStack>> equipment = new ArrayList<>();

        public Builder(Location location) {
            this.location = location;

            ServerLevel nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

            profile = new GameProfile(UUID.randomUUID(), UUID.randomUUID().toString().substring(0, 16).replaceAll("-", ""));
            ClientInformation clientInfo = ClientInformation.createDefault();
            ServerPlayer npcPlayer = new ServerPlayer(nmsWorld.getServer(), nmsWorld, profile, clientInfo);

            try {
                EmptyConnection conn = new EmptyConnection(PacketFlow.CLIENTBOUND);
                EmptyPacketListener connection = new EmptyPacketListener(nmsWorld.getServer(), conn, npcPlayer, new CommonListenerCookie(profile, 0, clientInfo, false));
                ReflectionUtils.setField(npcPlayer, "f", connection);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            this.npc = npcPlayer;
            this.entityId = npcPlayer.getId();

            /*org.bukkit.inventory.ItemStack air = new org.bukkit.inventory.ItemStack(Material.AIR);
            this.equipment = List.of(
                    Pair.of(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(air)),
                    Pair.of(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(air)),
                    Pair.of(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(air)),
                    Pair.of(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(air)),
                    Pair.of(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(air)),
                    Pair.of(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(air))
            );*/
        }

        public Builder skin(String texture, String signature) {
            profile.getProperties().get("textures").clear();
            profile.getProperties().put("textures", new Property("textures", texture, signature));
            return this;
        }

        public Builder equipment(org.bukkit.inventory.EquipmentSlot slot, org.bukkit.inventory.ItemStack item) {
            Validate.notNull(item, "Item cannot be null!");
            equipment.add(Pair.of(CraftEquipmentSlot.getNMS(slot), CraftItemStack.asNMSCopy(item)));
            return this;
        }

        /**
         * Overrides entity id of the npc to match an existing one
         * this way the entity appears to be an npc for the client
         */
        public Builder entityId(int id) {
            entityId = id;
            return this;
        }

        public NPC build() {
            return new NPC(this);
        }
    }
}
