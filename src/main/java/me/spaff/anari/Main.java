package me.spaff.anari;

import me.spaff.anari.cmd.AnariCommands;
import me.spaff.anari.indicator.DamageIndicator;
import me.spaff.anari.item.AnariItems;
import me.spaff.anari.listeners.PlayerListeners;
import me.spaff.anari.listeners.ServerListeners;
import me.spaff.anari.nms.PacketReader;
import me.spaff.anari.player.AnariPlayerManager;
import me.spaff.anari.utils.logger.ALevel;
import me.spaff.anari.utils.logger.ALogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static final String version = "0.0.1";
    private static Main instance;

    public Main() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Inject players
        PacketReader.pipelineHandler();

        registerListeners();
        registerCommands();

        AnariItems.registerItems();

        AnariPlayerManager.registerPlayers();
        AnariPlayerManager.startMenuTask();

        // TODO: Update players' inventory items
    }

    @Override
    public void onDisable() {
        // Uninject players
        //PacketReader.pipelineHandler();
        //DamageIndicator.removeAll();

        instance = null;
    }

    // Register functions

    private void registerListeners() {
        ALogger.log(ALevel.INFO, "Registering listeners...");

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ServerListeners(), this);
    }

    private void registerCommands() {
        ALogger.log(ALevel.INFO, "Registering commands...");
        this.getCommand("anari").setExecutor(new AnariCommands());
    }
}
