package org.screamingsandals.lib.gamecore.utils;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.gamecore.core.GameState;
import org.screamingsandals.lib.tasker.TaskerTime;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.m;
import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

public class GameUtils {

    private GameUtils() {
    }

    public static ItemStack buildStack(Material material, String title) {
        return buildStack(material, title, Collections.emptyList());
    }

    public static ItemStack buildStack(Material material, String title, List<String> lore) {
        Preconditions.checkNotNull(material, "material");
        Preconditions.checkNotNull(title, "title");
        Preconditions.checkNotNull(lore, "lore");

        final var stack = new ItemStack(material);
        final var meta = stack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public static String convertTimeUnitToLanguage(int period, TaskerTime taskerTime) {
        switch (taskerTime) {
            case TICKS: {
                if (period <= 1) {
                    return m("general.time-units.tick").get();
                } else {
                    return m("general.time-units.ticks").get();
                }
            }
            case SECONDS: {
                if (period <= 1) {
                    return m("general.time-units.second").get();
                } else {
                    return m("general.time-units.seconds").get();
                }
            }
            case MINUTES: {
                if (period <= 1) {
                    return m("general.time-units.minute").get();
                } else {
                    return m("general.time-units.minutes").get();
                }
            }
        }
        return taskerTime.toString();
    }

    public static String getTranslatedGameState(GameState gameState) {
        return m("core.state." + gameState.getName()).get();
    }

    public static String convertNullToLanguage() {
        return m("general.null-translated", "Nothing").get();
    }

    public static String getInfinityLanguage() {
        return m("general.infinity", "Infinity").get();
    }

    public static boolean canCastToInt(String value, Player player) {
        try {
            final var converted = Integer.parseInt(value);
            return converted >= 1;
        } catch (Exception e) {
            mpr("general.errors.invalid-number")
                    .replace("%entry%", value)
                    .send(player);
            return false;
        }
    }

    public static int castToInt(String value) {
        return Integer.parseInt(value);
    }

    public static boolean isInGameBorder(Location location, Location border1, Location border2) {
        if (!border1.getWorld().equals(location.getWorld())) {
            return false;
        }

        final var min = new Location(border1.getWorld(), Math.min(border1.getX(), border2.getX()), Math.min(border1.getY(), border2.getY()),
                Math.min(border1.getZ(), border2.getZ()));
        final var max = new Location(border1.getWorld(), Math.max(border1.getX(), border2.getX()), Math.max(border1.getY(), border2.getY()),
                Math.max(border1.getZ(), border2.getZ()));
        return (min.getX() <= location.getX() && min.getY() <= location.getY() && min.getZ() <= location.getZ() && max.getX() >= location.getX()
                && max.getY() >= location.getY() && max.getZ() >= location.getZ());
    }

    public static Optional<BarColor> getBarColorByString(String input) {
        try {
            return Optional.of(BarColor.valueOf(input.toUpperCase()));
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }
}
