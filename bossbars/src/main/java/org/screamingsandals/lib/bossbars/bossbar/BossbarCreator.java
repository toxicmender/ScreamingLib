package org.screamingsandals.lib.bossbars.bossbar;

import lombok.Data;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;

@Data
public class BossbarCreator {
    private Bossbar bossbar;

    public BossbarCreator() {
        bossbar = new Bossbar();
        bossbar.getBossbarHolder().setVisible(true);
    }

    public static BossbarCreator get() {
        return new BossbarCreator();
    }

    public static Bossbar get(String name, String title) {
        var bossbarCreator = get();
        var bossbarHolder = bossbarCreator.getBossbar().getBossbarHolder();

        bossbarCreator.getBossbar().setName(name);
        bossbarHolder.setTitle(title);
        return bossbarCreator.getBossbar();
    }

    public static Bossbar get(String name, String title, BarColor barColor, BarStyle barStyle) {
        var bossbarCreator = get();
        var bossbarHolder = bossbarCreator.getBossbar().getBossbarHolder();

        bossbarCreator.getBossbar().setName(name);
        bossbarHolder.setTitle(title);
        bossbarHolder.setBarColor(barColor);
        bossbarHolder.setBarStyle(barStyle);
        return bossbarCreator.getBossbar();
    }

    public static Bossbar get(String name, String title, BarColor barColor, BarStyle barStyle, BarFlag barFlag) {
        var bossbarCreator = get();
        var bossbarHolder = bossbarCreator.getBossbar().getBossbarHolder();

        bossbarCreator.getBossbar().setName(name);
        bossbarHolder.setTitle(title);
        bossbarHolder.setBarColor(barColor);
        bossbarHolder.setBarStyle(barStyle);
        bossbarHolder.addFlag(barFlag);
        return bossbarCreator.getBossbar();
    }
}