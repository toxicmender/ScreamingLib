package org.screamingsandals.lib.gamecore.core.phase;

import lombok.Data;
import org.screamingsandals.lib.gamecore.core.GameFrame;
import org.screamingsandals.lib.gamecore.core.GameState;
import org.screamingsandals.lib.gamecore.core.cycle.GameCycle;

@Data
public abstract class GamePhase {
    protected final transient GameCycle gameCycle;
    protected final transient GameFrame gameFrame;
    protected int runTime;
    protected int elapsedTime = 0;
    protected GameState phaseType;
    protected boolean oneTick = false; //if phase is only one tick long

    protected boolean firstTick = true;
    protected boolean finished;

    public GamePhase(GameCycle gameCycle) {
        this(gameCycle, -1);
    }

    public GamePhase(GameCycle gameCycle, int runTime) {
        this.gameCycle = gameCycle;
        this.gameFrame = gameCycle.getGameFrame();
        this.runTime = runTime;

        prepare(gameFrame);
    }

    public boolean preTick() {
        return true;
    }

    public void tick() {
        updatePlaceholders();

        if (firstTick) {
            onFirstTick();
        }

        elapsedTime++;

        if (oneTick) {
            finished = true;
            return;
        }

        finished = isCountdownFinished();
    }

    public void prepare(GameFrame gameFrame) {

    }

    public void onFirstTick() {

    }

    public boolean isFinished() {
        return finished;
    }

    private boolean isCountdownFinished() {
        if (runTime == -1) { //infinity ticking
            return false;
        }

        return elapsedTime == runTime;
    }

    public int countRemainingTime() {
        if (runTime == -1 && elapsedTime == 1) {
            return 0;
        }

        return runTime - elapsedTime;
    }

    public void reset() {
        elapsedTime = 0;
        finished = false;
        firstTick = true;
    }

    public void updatePlaceholders() {
    }

}
