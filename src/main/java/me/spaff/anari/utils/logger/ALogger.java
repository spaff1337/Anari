package me.spaff.anari.utils.logger;

import me.spaff.anari.utils.StringUtils;
import org.bukkit.Bukkit;

public class ALogger {
    public static void log(ALevel level, String msg) {
        Bukkit.getServer().getConsoleSender().sendMessage(StringUtils.getColoredText("&b[Anari] " + level.getColor() + msg));
    }
}
