package org.screamingsandals.lib.bossbars.bossbar;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Data
public class BossbarHolder{
    private String title = "";
    private boolean visible;
    private BarColor barColor = BarColor.PURPLE;
    private BarStyle barStyle = BarStyle.SOLID;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private List<BarFlag> barFlags = new LinkedList<>();

    private transient double progress;
    private transient BossBar bukkitBossbar;

    public BossbarHolder() {
        bukkitBossbar = Bukkit.createBossBar(title, barColor, barStyle, barFlags.toArray(new BarFlag[0]));
    }

    public void setTitle(String title) {
        this.title = title;
        bukkitBossbar.setTitle(title);
    }

    public void setBarColor(BarColor barColor) {
        this.barColor = barColor;
        bukkitBossbar.setColor(barColor);
    }

    public void setBarStyle(BarStyle barStyle) {
        this.barStyle = barStyle;
        bukkitBossbar.setStyle(barStyle);
    }

    public void addFlag(BarFlag barFlag) {
        if (!barFlags.contains(barFlag)) {
            barFlags.add(barFlag);
        }

        if (!bukkitBossbar.hasFlag(barFlag)) {
            bukkitBossbar.addFlag(barFlag);
        }
    }

    public void removeFlag(BarFlag barFlag) {
        barFlags.remove(barFlag);

        if (bukkitBossbar.hasFlag(barFlag)) {
            bukkitBossbar.removeFlag(barFlag);
        }
    }

    public void addFlag(List<BarFlag> barFlags) {
        barFlags.forEach(this::addFlag);
    }

    public void removeFlag(List<BarFlag> barFlags) {
        barFlags.forEach(this::removeFlag);
    }

    public void addFlag(BarFlag[] barFlags) {
        addFlag(Arrays.asList(barFlags));
    }

    public void removeAllFlags() {
        removeFlag(Arrays.asList(BarFlag.values()));
    }

    public List<BarFlag> getActiveFlags() {
        return new ArrayList<>(barFlags);
    }

    public List<Player> getViewers() {
        return new ArrayList<>(bukkitBossbar.getPlayers());
    }

    public void addViewer(Player player) {
        bukkitBossbar.addPlayer(player);
    }

    public void removeViewer() {
        bukkitBossbar.removeAll();
    }

    public void setVisible(boolean visible) {
        bukkitBossbar.setVisible(visible);
    }

    public void setProgress(double value) {
        this.progress = value;
        var editableProgress = value;

        if (editableProgress > 100) {
            editableProgress = 100;
        } else if (editableProgress < 0) {
            editableProgress = 0;
        }

        bukkitBossbar.setProgress(editableProgress / 100);
    }

    public void setProgress(double value, double max) {
        if (max == -1) {
            setProgress(100);
            return;
        }

        final var toSet = (value / max) * 100;
        setProgress(toSet);
    }

    public void updateAll() {
        bukkitBossbar.setTitle(title);
        bukkitBossbar.setColor(barColor);
        bukkitBossbar.setProgress(progress);
        bukkitBossbar.setStyle(barStyle);
    }
}
