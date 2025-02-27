package me.spaff.anari.utils;

import com.google.common.base.Strings;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.spaff.anari.Main;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class StringUtils {
    public static void sendMessage(Player player, String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        player.sendMessage(text);
    }

    public static String getColoredText(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String getHexColor(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color format. Must be 6 characters.");
        }

        StringBuilder colorCode = new StringBuilder("§x");
        for (char c : hex.toCharArray()) {
            colorCode.append('§').append(c);
        }

        return ChatColor.translateAlternateColorCodes('&', String.valueOf(colorCode));
    }

    public static String getHexColorText(String hex, String text) {
        return getHexColor(hex) + text;
    }

    public static String stripColors(String text) {
        Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('&') + "[0-9A-FK-OR]");
        return text == null ? null : STRIP_COLOR_PATTERN.matcher(text).replaceAll("");
    }

    public static String wrapString(String str, int length) {
        return WordUtils.wrap(str, length, "\n", false);
    }

    public static String wrapString(String str) {
        return WordUtils.wrap(str, 38, "\n", false);
    }

    public static String formatTime(int value) {
        int seconds = value % 60;
        int minutes = value / 60;

        String time;
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes %= 60;

            time = String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        else
            time = String.format("%d:%02d", minutes, seconds);

        return time;
    }

    public static String getProgressBar(double current, double max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        double percent = current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, Math.min(progressBars, totalBars))
                + Strings.repeat("" + notCompletedColor + symbol, Math.max((totalBars - progressBars), 0));
    }

    public static String formatNumber(int value) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
        return df.format(value);
    }

    public static String decimalFormat(float value) {
        DecimalFormat format2 = new DecimalFormat("#,###.##");
        format2.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        return format2.format(value);
    }

    public static String decimalFormat(double value, int digitPlace) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(digitPlace);
        return df.format(value);
    }

    public static String convertToRoman(int value) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanLetters = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        StringBuilder roman = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (value >= values[i]) {
                value = value - values[i];
                roman.append(romanLetters[i]);
            }
        }
        return roman.toString();
    }
}
