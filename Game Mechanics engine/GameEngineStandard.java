package fr.tvbarthel.games.GhostHunter.mechanics.engine;


import android.content.Context;

import fr.tvbarthel.games.GhostHunter.mechanics.behaviors.GameBehaviorStandard;
import fr.tvbarthel.games.GhostHunter.model.TargetableItem;
import fr.tvbarthel.games.GhostHunter.ui.gameviews.GameView;
import fr.tvbarthel.games.GhostHunter.ui.gameviews.GameViewStandard;

public abstract class GameEngineStandard extends GameEngine {
    protected GameBehaviorStandard mGameBehavior;
    protected GameViewStandard mGameView;

    public GameEngineStandard(Context context, IGameEngine iGameEngine, GameBehaviorStandard gameBehavior) {
        super(context, iGameEngine, gameBehavior);
        mGameBehavior = gameBehavior;
    }

    public int getCurrentAmmunition() {
        return mGameBehavior.getCurrentAmmunition();
    }

    public int getCurrentCombo() {
        return mGameBehavior.getCurrentCombo();
    }

    public int getCurrentScore() {
        return mGameBehavior.getCurrentScore();
    }

    protected void setGameView(GameView gameView) {
        super.setGameView(gameView);
        mGameView = (GameViewStandard) gameView;
    }

    public void onTargetKilled(TargetableItem target) {
        mGameView.animateDyingGhost(target);
    }
}
