package org.screamingsandals.lib.nms.holograms;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.lib.nms.network.inbound.AutoPacketInboundListener;

import java.util.ArrayList;
import java.util.List;

import static org.screamingsandals.lib.nms.utils.ClassStorage.NMS.PacketPlayInUseEntity;
import static org.screamingsandals.lib.reflection.Reflection.getField;

@Data
public class HologramManager implements Listener {
    public static final int VISIBILITY_DISTANCE_SQUARED = 4096;

    private final Plugin plugin;
    private final List<Hologram> activeHolograms = new ArrayList<>();
    private final AutoPacketInboundListener packetInboundListener;

    public HologramManager(Plugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

		packetInboundListener = new AutoPacketInboundListener(plugin) {

            @Override
            protected Object handle(Player sender, Object packet) {
                if (PacketPlayInUseEntity.isInstance(packet)) {
                    final var field = (int) getField(PacketPlayInUseEntity, "a,field_149567_a", packet);
                    for (var hologram : activeHolograms) {
                        if (hologram.handleTouch(sender, field)) {
                            break;
                        }
                    }
                }
                return packet;
            }
        };
    }

    public Hologram spawnHologram(Player player, Location loc, List<String> lines) {
        return spawnHologram(List.of(player), loc, lines);
    }

    public Hologram spawnHologramTouchable(Player player, Location loc, List<String> lines) {
        return spawnHologramTouchable(List.of(player), loc, lines);
    }

    public Hologram spawnHologram(List<Player> players, Location loc, List<String> lines) {
        return new Hologram(this, players, loc, lines);
    }

    public Hologram spawnHologramTouchable(List<Player> players, Location loc, List<String> lines) {
        return new Hologram(this, players, loc, lines, true);
    }

    public void destroy() {
    	activeHolograms.forEach(Hologram::destroy);
	}

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (activeHolograms.isEmpty()) {
            return;
        }

        final var copy = new ArrayList<>(activeHolograms);
        for (var hologram : copy) {
            if (hologram.isEmpty() || !hologram.hasViewers()) {
                activeHolograms.remove(hologram);
                continue;
            }
            try {
                final var player = event.getPlayer();
                final var viewers = hologram.getViewers();
                final var location = hologram.getLocation();

                if (viewers.contains(player) && player.getWorld().equals(location.getWorld())) {
                    if (event.getTo().distanceSquared(location) < VISIBILITY_DISTANCE_SQUARED
                            && event.getFrom().distanceSquared(location) >= VISIBILITY_DISTANCE_SQUARED) {
                        hologram.update(player, hologram.getAllSpawnPackets(), false);
                    } else if (event.getTo().distanceSquared(location) >= VISIBILITY_DISTANCE_SQUARED
                            && event.getFrom().distanceSquared(location) < VISIBILITY_DISTANCE_SQUARED) {
                        hologram.update(player, List.of(hologram.getFullDestroyPacket()), false);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (activeHolograms.isEmpty()) {
            return;
        }

        final var copy = new ArrayList<>(activeHolograms);
        for (Hologram hologram : copy) {
            if (hologram.isEmpty() || !hologram.hasViewers()) {
                activeHolograms.remove(hologram);
                continue;
            }
            try {
                final var player = event.getPlayer();
                final var viewers = hologram.getViewers();
                final var loc = hologram.getLocation();

                if (viewers.contains(player) && player.getWorld().equals(loc.getWorld())
                        && !event.getFrom().equals(loc.getWorld())) {
                    if (player.getLocation().distanceSquared(loc) < VISIBILITY_DISTANCE_SQUARED) {
                        hologram.update(player, hologram.getAllSpawnPackets(), false);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (activeHolograms.isEmpty()) {
            return;
        }

        final var copy = new ArrayList<>(activeHolograms);
        for (Hologram hologram : copy) {
            if (hologram.isEmpty() || !hologram.hasViewers()) {
                activeHolograms.remove(hologram);
                continue;
            }
            try {
                final var player = event.getPlayer();
                final var viewers = hologram.getViewers();
                final var location = hologram.getLocation();

                if (viewers.contains(player) && event.getRespawnLocation().getWorld().equals(location.getWorld())) {
                    if (player.getLocation().distanceSquared(location) < VISIBILITY_DISTANCE_SQUARED) {
                        new BukkitRunnable() {
                            public void run() {
                                try {
                                    hologram.update(player, hologram.getAllSpawnPackets(), false);
                                } catch (Throwable ignored) {
                                }
                            }
                        }.runTaskLater(plugin, 20L);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (activeHolograms.isEmpty() || !event.getFrom().getWorld().equals(event.getTo().getWorld()) /* World change is handled in another event*/) {
            return;
        }

        final var copy = new ArrayList<>(activeHolograms);
        for (Hologram hologram : copy) {
            if (hologram.isEmpty() || !hologram.hasViewers()) {
                activeHolograms.remove(hologram);
                continue;
            }
            try {
                final var player = event.getPlayer();
                final var viewers = hologram.getViewers();
                final var location = hologram.getLocation();

                if (viewers.contains(player) && player.getWorld().equals(location.getWorld())) {
                    if (event.getTo().distanceSquared(location) < VISIBILITY_DISTANCE_SQUARED
                            && event.getFrom().distanceSquared(location) >= VISIBILITY_DISTANCE_SQUARED) {
                        hologram.update(player, hologram.getAllSpawnPackets(), false);
                    } else if (event.getTo().distanceSquared(location) >= VISIBILITY_DISTANCE_SQUARED
                            && event.getFrom().distanceSquared(location) < VISIBILITY_DISTANCE_SQUARED) {
                        hologram.update(player, List.of(hologram.getFullDestroyPacket()), false);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
