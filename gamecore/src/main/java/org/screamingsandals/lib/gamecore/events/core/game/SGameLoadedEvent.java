package org.screamingsandals.lib.gamecore.events.core.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.lib.gamecore.core.GameFrame;

@EqualsAndHashCode(callSuper = false)
@Data
public class SGameLoadedEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final GameFrame gameFrame;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}