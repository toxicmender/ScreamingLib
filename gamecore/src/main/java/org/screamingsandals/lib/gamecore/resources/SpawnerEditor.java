package org.screamingsandals.lib.gamecore.resources;

import lombok.Data;
import org.bukkit.entity.Player;
import org.screamingsandals.lib.gamecore.core.GameBuilder;
import org.screamingsandals.lib.gamecore.visuals.holograms.GameHologram;

/**
 * Spawner editing. What more to say?
 */
@Data
public class SpawnerEditor {
    private final Player player;
    private final GameBuilder<?> gameBuilder;
    private final ResourceSpawner originalSpawner;
    private final ResourceSpawner resourceSpawner;
    private final GameHologram gameHologram;

    public SpawnerEditor(Player player, GameBuilder<?> gameBuilder, ResourceSpawner resourceSpawner, GameHologram gameHologram) throws CloneNotSupportedException {
        this.player = player;
        this.gameBuilder = gameBuilder;
        this.originalSpawner = resourceSpawner;
        this.resourceSpawner = (ResourceSpawner) resourceSpawner.clone();
        this.gameHologram = gameHologram;
    }

    public void save() {
        final var resourceManager = gameBuilder.getGameFrame().getResourceManager();
        resourceManager.unregister(resourceSpawner.getUuid());
        resourceManager.register(resourceSpawner);

        end();
    }

    public void end() {
        gameBuilder.setSpawnerEditor(null);
    }
}
