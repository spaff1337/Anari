package me.spaff.anari.player;

import me.spaff.anari.Constants;
import me.spaff.anari.Main;
import me.spaff.anari.utils.ItemBuilder;
import me.spaff.anari.utils.ItemUtils;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.inventory.InventoryMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AnariPlayerManager {
    private static final Map<UUID, AnariPlayer> anariPlayers = new HashMap<>();

    public static void registerPlayer(Player player) {
        anariPlayers.putIfAbsent(player.getUniqueId(), new AnariPlayer(player));
    }

    public static void registerPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            registerPlayer(player);
        }
    }

    public static void unregisterPlayer(Player player) {
        anariPlayers.remove(player.getUniqueId());
    }

    public static void unregisterPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            unregisterPlayer(player);
        }
    }

    public static AnariPlayer getPlayer(Player player) {
        return anariPlayers.getOrDefault(player.getUniqueId(), new AnariPlayer(player));
    }

    public static AnariPlayer getPlayer(UUID uuid) {
        return anariPlayers.getOrDefault(uuid, new AnariPlayer(uuid));
    }

    public static void startMenuTask() {
        ItemStack menuIcon = new ItemBuilder.Builder(Material.PLAYER_HEAD)
                .name("&eAnari Menu")
                .lore(List.of(
                        "&7Click to access your",
                        "&eSkills &7and &bAccessories&7."
                ))
                .build()
                .getItem();

        ItemUtils.setSkin(menuIcon, Constants.MENU_ICON_SKIN);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    InventoryMenu craftingMenu = (((CraftPlayer) player).getHandle()).inventoryMenu;
                    ItemStack resultItem = CraftItemStack.asBukkitCopy(craftingMenu.getResultSlot().getItem());

                    if (resultItem.getType().equals(Material.AIR))
                        sendSetSlotPacket(player, menuIcon);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 5);
    }

    private static void sendSetSlotPacket(Player player, ItemStack item) {
        InventoryMenu menu = (((CraftPlayer) player).getHandle()).inventoryMenu;

        ClientboundContainerSetSlotPacket setSlotPacket =
                new ClientboundContainerSetSlotPacket(menu.containerId, menu.getStateId(),
                        0, CraftItemStack.asNMSCopy(item));

        ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;
        connection.send(setSlotPacket);
    }
}
