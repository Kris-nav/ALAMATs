package game.battle;

import java.util.ArrayList;

public interface BattleCallback {
    void onComplete(Fighter updatedFighter, ArrayList<Fighter> updatedTeam,
                    int updatedScrolls, int updatedLunas, int updatedPotions,
                    boolean blackout);
}