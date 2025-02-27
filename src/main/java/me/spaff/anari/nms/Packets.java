package me.spaff.anari.nms;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import me.spaff.anari.utils.ReflectionUtils;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

public class Packets {
    private static final Map<String, EntityDataAccessor<?>> cachedAccessors = new HashMap<>();

    public static void sendRemoveEntityPacket(Player player, Entity entity) {
        ServerGamePacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;
        connection.send(new ClientboundRemoveEntitiesPacket(entity.getId()));
    }

    public static void sendAddEntityPacket(Player player, Entity entity, Location location) {
        sendAddEntityPacket(player, entity, location, entity.getId());
    }

    public static void sendAddEntityPacket(Player player, Entity entity, Location location, int entityId) {
        ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                entityId,
                entity.getUUID(),
                location.getX(),
                location.getY(),
                location.getZ(),
                0, // Pitch
                location.getYaw(), // Yaw
                entity.getType(),
                0,
                Vec3.ZERO,
                location.getYaw() // Yaw
        );

        connection.send(addEntityPacket);
    }

    public static void sendPlayerInfoPacket(Player player, ServerPlayer npc, ClientboundPlayerInfoUpdatePacket.Action action) {
        ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

        ClientboundPlayerInfoUpdatePacket packet =
                new ClientboundPlayerInfoUpdatePacket(action, npc);

        connection.send(packet);
    }

    public static void sendEquipmentPacket(Player player, int entityId, List<Pair<EquipmentSlot, ItemStack>> equipment) {
        ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

        ClientboundSetEquipmentPacket equipmentPacket =
                new ClientboundSetEquipmentPacket(entityId, equipment); // List.of(Pair.of(slot, CraftItemStack.asNMSCopy(item)))

        connection.send(equipmentPacket);
    }

    public static void sendEquipmentPacket(Player player, Entity entity, List<Pair<EquipmentSlot, ItemStack>> equipment) {
        sendEquipmentPacket(player, entity.getId(), equipment);
    }

    public static void sendAnimatePacket(Player player, Entity entity, NPCAction action) {
        ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

        ClientboundAnimatePacket animatePacket =
                new ClientboundAnimatePacket(entity, action.getActionId());

        connection.send(animatePacket);
    }

    public static void sendAttributePacket(Player player, Entity entity, AttributeInstance attributeInstance) {
        /*ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

        // Attributes.SCALE
        Holder<Holder<Attribute>> holder = new Holder<>(Attributes.SCALE);
        new AttributeInstance(holder, (Consumer<AttributeInstance>) List.of(attributeInstance));

        Collection<AttributeInstance> attributeInstances = new ArrayList<>();
        attributeInstances.add(attributeInstance);

        ClientboundUpdateAttributesPacket attributesPacket =
                new ClientboundUpdateAttributesPacket(entity.getId(), attributeInstances);

        connection.send(attributesPacket);*/
    }

    /*public static void sendMobEffectPacket(Player player, Entity entity) {
        ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

        MobEffectInstance mobEffectInstance = new MobEffectInstance(MobEffects.INVISIBILITY, 99999, 0, false, true, false);

        ClientboundUpdateMobEffectPacket mobEffectPacket =
                new ClientboundUpdateMobEffectPacket(entity.getId(), mobEffectInstance, false);

        //
        *//*Map<Holder<MobEffect>, MobEffectInstance> activeEffects = Maps.newHashMap();
        activeEffects.put(MobEffects.INVISIBILITY, mobEffectInstance);

        ReflectionUtils.setField(entity, "bT", activeEffects); // bT -> activeEffects*//*
        //

        connection.send(mobEffectPacket);
    }*/

    public static void sendTeamAddPacket(Player player, String playerName) {
        ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

        Scoreboard scoreboard = new Scoreboard();
        PlayerTeam team = new PlayerTeam(scoreboard, "[NPC]");
        team.setNameTagVisibility(Team.Visibility.NEVER);
        //team.setSeeFriendlyInvisibles(true);

        // ClientboundSetPlayerTeamPacket.Parameters parameter = new ClientboundSetPlayerTeamPacket.Parameters(team);
        // "[NPC]", 0, Optional.of(parameter), List.of(profile.getName())); // 0 -> METHOD_ADD

        /*String[] teamMembers = new String[]{player.getName(), playerName};
        for (String name : teamMembers) {
            ClientboundSetPlayerTeamPacket teamPacket =
                    ClientboundSetPlayerTeamPacket.createPlayerPacket(team, name, ClientboundSetPlayerTeamPacket.Action.ADD);

            ReflectionUtils.setField(teamPacket, "i", 0); // i -> method, 0 -> METHOD_ADD

            ClientboundSetPlayerTeamPacket.Parameters parameter = new ClientboundSetPlayerTeamPacket.Parameters(team);
            ReflectionUtils.setField(teamPacket, "l", Optional.of(parameter)); // l -> parameters

            connection.send(teamPacket);
        }*/

        ClientboundSetPlayerTeamPacket teamPacket =
                ClientboundSetPlayerTeamPacket.createPlayerPacket(team, playerName, ClientboundSetPlayerTeamPacket.Action.ADD);

        ReflectionUtils.setField(teamPacket, "i", 0); // i -> method, 0 -> METHOD_ADD

        ClientboundSetPlayerTeamPacket.Parameters parameter = new ClientboundSetPlayerTeamPacket.Parameters(team);
        ReflectionUtils.setField(teamPacket, "l", Optional.of(parameter)); // l -> parameters

        /*System.out.println("getName: " + teamPacket.getName());
        System.out.println("getTeamAction: " + teamPacket.getTeamAction());
        System.out.println("getPlayerAction: " + teamPacket.getPlayerAction());
        System.out.println("getPlayers: " + teamPacket.getPlayers());
        System.out.println("getParameters: " + teamPacket.getParameters());*/

        connection.send(teamPacket);
    }

    public static void sendTeleportPacket(Player player, Entity entity, Location location) {
        ServerGamePacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

        PositionMoveRotation posRot = new PositionMoveRotation(
                new Vec3(location.getX(), location.getY(), location.getZ()),
                new Vec3(0, 0, 0),
                location.getYaw(),
                location.getPitch()
        );

        Set<Relative> relatives = EnumSet.noneOf(Relative.class);
        connection.send(new ClientboundTeleportEntityPacket(entity.getId(), posRot, relatives, true));
    }

    public static <T> void sendDisplayDataPacket(Player player, Display displayEntity, Class<?> classField, String fieldName, T value) {
        ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

        String cacheKey = classField.getName() + "." + fieldName;
        EntityDataAccessor<T> accessor = (EntityDataAccessor<T>) cachedAccessors.get(cacheKey);

        if (accessor == null) {
            try {
                Field field = classField.getDeclaredField(fieldName);
                field.setAccessible(true);

                accessor = (EntityDataAccessor<T>) field.get(null);
                cachedAccessors.put(cacheKey, accessor);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }

        SynchedEntityData.DataItem<T> dataItem = new SynchedEntityData.DataItem<>(accessor, value);
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(
                displayEntity.getId(),
                List.of(dataItem.value())
        );

        connection.send(packet);
    }
}
