package game.battle;

import java.util.ArrayList;

// ✅ blackout = true means all fighters fainted
public interface BattleCallback {
    void onComplete(Fighter updatedFighter, ArrayList<Fighter> updatedTeam,
                    int updatedScrolls, boolean blackout);
}