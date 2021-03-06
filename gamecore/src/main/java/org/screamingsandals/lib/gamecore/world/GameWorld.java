package org.screamingsandals.lib.gamecore.world;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.lib.gamecore.adapter.LocationAdapter;

@EqualsAndHashCode(callSuper = false)
@Data
public class GameWorld extends BaseWorld {
    private LocationAdapter spectatorSpawn;

    public GameWorld(String worldName) {
        super(worldName);
    }
}
