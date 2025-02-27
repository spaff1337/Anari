package me.spaff.anari.indicator;

import me.spaff.anari.Main;
import me.spaff.anari.nms.DisplayEntity;
import me.spaff.anari.utils.MathUtils;
import me.spaff.anari.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DamageIndicator {
    private static List<DamageIndicator> indicators = new ArrayList<>();

    private DisplayEntity textDisplay;

    private String text;
    private Location location;
    private int lifeTimeTicks;

    public DamageIndicator(double damage, Location location) {
        this.text = "&6" + StringUtils.decimalFormat(damage, 1);
        this.lifeTimeTicks = 15;

        double randomX = ThreadLocalRandom.current().nextDouble(-0.6, 0.6);
        double randomY = ThreadLocalRandom.current().nextDouble(-0.35, 1);
        double randomZ = ThreadLocalRandom.current().nextDouble(-0.6, 0.6);
        location.add(randomX, randomY, randomZ);

        this.location = location;

        this.textDisplay = new DisplayEntity.TextDisplay(location)
                .scale(new Vector3f(1.5F, 1.5F, 1.5F))
                .displayText(text)
                .backgroundColor(0, 0,0,69)
                .show();
    }

    // TODO: Add limit
    public void spawn() {
        indicators.add(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                remove();
            }
        }.runTaskLater(Main.getInstance(), lifeTimeTicks);
    }

    public void remove() {
        textDisplay.remove();
        indicators.remove(this);
    }

    public static void removeAll() {
        for (DamageIndicator dmgIndicator : indicators) {
            dmgIndicator.remove();
        }
    }

    // TODO: Improved animation, add flashing to the text
    /*private void remove() {
        new BukkitRunnable() {
            double progress = 1.0D;

            @Override
            public void run() {
                if (progress <= 0.0D) {
                    this.cancel();
                    textDisplay.remove();
                    return;
                }

                float scaleCalc = MathUtils.Easing.easeOutExpo.getFunction().apply(progress).floatValue();
                System.out.println("scaleCalc: " + scaleCalc);

                float newScale = (textDisplay.getScale().x - 0.125f) * scaleCalc;
                System.out.println("newScale: " + newScale);

                DisplayEntity.TextDisplay display = (DisplayEntity.TextDisplay) textDisplay.getDisplay();
                display.updateScale(new Vector3f(newScale, newScale, newScale));

                progress -= 0.15D;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }*/
}