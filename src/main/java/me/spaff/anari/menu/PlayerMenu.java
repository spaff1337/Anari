package me.spaff.anari.menu;

import me.spaff.anari.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerMenu extends Menu implements Listener {
    private Player player;

    public PlayerMenu(Player player, String title, int rows) {
        super(title, rows);
        this.player = player;
    }

    protected void register() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    protected void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void open() {
        player.openInventory(this.getInventory());
    }

    public void fillWith(ItemStack item) {
        for (int i = 0; i < (getRows() * 9); i++) {
            this.setItem(i, item);
        }
    }

    public Player getPlayer() {
        return player;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if ((player.getUniqueId().equals(e.getPlayer().getUniqueId())
                && e.getInventory().equals(this.getInventory())))
            unregister();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (player.getUniqueId().equals(e.getPlayer().getUniqueId()))
            unregister();
    }
}
