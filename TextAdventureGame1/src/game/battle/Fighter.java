package game.battle;

import java.util.ArrayList;
import java.util.List;

public class Fighter {
    public String name;
    public String sprite;
    public String back_sprite;
    public ArrayList<Type> types;
    public ArrayList<Stat> stats;
    public ArrayList<Move> moveset;
    public ArrayList<Move> allMoves;
    public ArrayList<Integer> moveUnlockLevels;

    public int level;
    public int exp;
    public int expToNext;

    public boolean fainted   = false;
    public boolean isStunned = false;
    public String  message   = "";

    public static int expNeeded(int fromLevel) {
        return Math.max(80, 80 + (fromLevel - 5) * 20);
    }

    public Fighter(String name, String sprite, String back_sprite,
                   ArrayList<Type> types,
                   ArrayList<Stat> stats,
                   ArrayList<Move> allMoves,
                   ArrayList<Integer> moveUnlockLevels,
                   int level) {
        this.name             = name;
        this.sprite           = sprite;
        this.back_sprite      = back_sprite;
        this.types            = types;
        this.stats            = stats;
        this.allMoves         = allMoves;
        this.moveUnlockLevels = moveUnlockLevels;
        this.level            = level;
        this.exp              = 0;
        this.expToNext        = expNeeded(level);

        this.moveset = new ArrayList<>();
        for (int i = 0; i < allMoves.size() && this.moveset.size() < 4; i++) {
            Move m   = allMoves.get(i);
            int  req = (i < moveUnlockLevels.size()) ? moveUnlockLevels.get(i) : 1;
            Move copy = new Move(m.name, m.type, m.damage, m.stat_changes, m.maxPp);
            if (level < req) {
                copy.lockedUntilLevel = req;
                copy.pp = 0;
            }
            this.moveset.add(copy);
        }

        while (this.moveset.size() < 4) {
            Type normalType = new Type("Normal", 0xAAAAAA);
            Move pad = new Move("---", normalType, 0, new ArrayList<>(), 1);
            pad.lockedUntilLevel = 999;
            this.moveset.add(pad);
        }
    }

    public Fighter(String name, String sprite, String back_sprite,
                   ArrayList<Type> types,
                   ArrayList<Stat> stats,
                   ArrayList<Move> moveset) {
        this.name             = name;
        this.sprite           = sprite;
        this.back_sprite      = back_sprite;
        this.types            = types;
        this.stats            = stats;
        this.moveset          = moveset;
        this.allMoves         = new ArrayList<>(moveset);
        this.moveUnlockLevels = new ArrayList<>();
        for (int i = 0; i < moveset.size(); i++) moveUnlockLevels.add(1);
        this.level     = 5;
        this.exp       = 0;
        this.expToNext = expNeeded(5);
    }

    public List<String> gainExp(int amount) {
        List<String> newMoves = new ArrayList<>();
        exp += amount;
        while (exp >= expToNext) {
            exp -= expToNext;
            level++;
            expToNext = expNeeded(level);
            scaleStatsOnLevelUp();
            newMoves.addAll(checkNewMoves());
        }
        return newMoves;
    }

    private void scaleStatsOnLevelUp() {
        if (stats.size() > 0) { stats.get(0).base += 10; stats.get(0).value += 10; }
        if (stats.size() > 1) { stats.get(1).base += 4;  stats.get(1).value += 4;  }
        if (stats.size() > 2) { stats.get(2).base += 3;  stats.get(2).value += 3;  }
        if (stats.size() > 3) { stats.get(3).base += 3;  stats.get(3).value += 3;  }
    }

    private List<String> checkNewMoves() {
        List<String> unlocked = new ArrayList<>();

        for (int i = 0; i < moveset.size(); i++) {
            Move mv = moveset.get(i);
            if (mv.isLocked() && mv.lockedUntilLevel != 999
                    && level >= mv.lockedUntilLevel) {
                mv.lockedUntilLevel = 0;
                mv.pp = mv.maxPp;
                unlocked.add(mv.name);
            }
        }

        for (int i = 0; i < allMoves.size(); i++) {
            int req = (i < moveUnlockLevels.size()) ? moveUnlockLevels.get(i) : 1;
            if (level == req) {
                Move m = allMoves.get(i);
                boolean already = moveset.stream()
                        .anyMatch(mv -> mv.name.equals(m.name));
                if (!already) {
                    boolean replaced = false;
                    for (int s = 0; s < moveset.size(); s++) {
                        if (moveset.get(s).name.equals("---")) {
                            Move copy = new Move(m.name, m.type,
                                    m.damage, m.stat_changes, m.maxPp);
                            moveset.set(s, copy);
                            replaced = true;
                            unlocked.add(m.name);
                            break;
                        }
                    }
                    if (!replaced && moveset.size() < 4) {
                        moveset.add(new Move(m.name, m.type,
                                m.damage, m.stat_changes, m.maxPp));
                        unlocked.add(m.name);
                    }
                }
            }
        }
        return unlocked;
    }

    public double getTypeMultiplier(String atkType, String defType) {
        switch (atkType) {
            case "Fire":
                if (defType.equals("Grass") || defType.equals("Wind"))  return 2.0;
                if (defType.equals("Water") || defType.equals("Earth"))  return 0.5;
                break;
            case "Water":
                if (defType.equals("Fire") || defType.equals("Earth"))   return 2.0;
                if (defType.equals("Grass") || defType.equals("Wind"))   return 0.5;
                break;
            case "Grass":
                if (defType.equals("Water") || defType.equals("Earth"))  return 2.0;
                if (defType.equals("Fire") || defType.equals("Wind"))    return 0.5;
                break;
            case "Shadow":
                if (defType.equals("Spirit"))  return 2.0;
                if (defType.equals("Normal"))  return 0.5;
                break;
            case "Spirit":
                if (defType.equals("Shadow"))  return 2.0;
                break;
            case "Wind":
                if (defType.equals("Earth"))   return 2.0;
                if (defType.equals("Fire"))    return 0.5;
                break;
            case "Earth":
                if (defType.equals("Fire") || defType.equals("Wind"))    return 2.0;
                if (defType.equals("Grass") || defType.equals("Water"))  return 0.5;
                break;
        }
        return 1.0;
    }

    public void useMove(Fighter target, int moveIndex) {
        if (moveIndex < 0 || moveIndex >= moveset.size()) return;
        Move move = moveset.get(moveIndex);
        if (move.isLocked()) return;

        // ── JABI: restore 50% of max HP ───────────────────────────
        if (move.name.equals("Jabi")) {
            double heal = stats.get(0).base * 0.50;
            stats.get(0).value = Math.min(stats.get(0).base,
                    stats.get(0).value + heal);
            message = "KHAIBALANG ate Jabi and restored HP!";
            return;
        }

        // ── SAY STOP: 70% chance to stun target ───────────────────
        if (move.name.equals("Say Stop")) {
            if (Math.random() < 0.70) {
                target.isStunned = true;
                message = target.name + " was stopped by Say Stop!";
            } else {
                message = "Say Stop had no effect!";
            }
            return;
        }

        double effectiveness = 1.0;
        if (target.types != null && move.type != null) {
            for (Type defType : target.types) {
                effectiveness *= getTypeMultiplier(move.type.name, defType.name);
            }
        }

        if (effectiveness == 0) {
            message = "It had no effect!";
            return;
        }

        double levelBonus   = 1.0 + (level * 0.05);
        double randomFactor = 0.85 + Math.random() * 0.15;
        double atkStat      = stats.size() > 1 ? stats.get(1).value : 100;
        double defStat      = target.stats.size() > 2 ? target.stats.get(2).value : 100;

        // ── Crit chance for Word of Day (12%) and Stomp (20%) ─────
        double critMult = 1.0;
        if (move.name.equals("Word of Day") && Math.random() < 0.12) {
            critMult = 1.5;
            message  = "A critical Word of Day!";
        }
        if (move.name.equals("Stomp") && Math.random() < 0.20) {
            critMult = 1.5;
            message  = "A critical Stomp!";
        }

        double damage = move.damage * levelBonus * effectiveness
                * randomFactor * critMult * (atkStat / Math.max(1, defStat));
        damage = Math.max(1, damage);

        target.stats.get(0).value = Math.max(0,
                target.stats.get(0).value - damage);

        if (critMult == 1.0) {
            if (effectiveness > 1.0)      message = "It's super effective!";
            else if (effectiveness < 1.0) message = "It's not very effective...";
        }

        if (target.stats.get(0).value <= 0) target.fainted = true;

        if (move.stat_changes != null) {
            for (int i = 0; i < move.stat_changes.size()
                    && i + 1 < stats.size(); i++) {
                stats.get(i + 1).value *= (1.0 + move.stat_changes.get(i));
            }
        }
    }

    public int chooseMove(Fighter target) {
        // Khaibalang AI: use Jabi when HP below 40%
        if (name.equals("KHAIBALANG")) {
            double hpRatio = stats.get(0).value / stats.get(0).base;
            if (hpRatio < 0.40) {
                for (int i = 0; i < moveset.size(); i++) {
                    if (moveset.get(i).name.equals("Jabi")
                            && moveset.get(i).pp > 0) return i;
                }
            }
        }

        ArrayList<Integer> available = new ArrayList<>();
        for (int i = 0; i < moveset.size(); i++) {
            Move m = moveset.get(i);
            if (!m.isLocked() && m.pp > 0 && m.damage > 0) available.add(i);
        }
        if (available.isEmpty()) {
            for (int i = 0; i < moveset.size(); i++) {
                if (!moveset.get(i).isLocked()
                        && moveset.get(i).pp > 0) return i;
            }
            return 0;
        }
        return available.get((int)(Math.random() * available.size()));
    }

    public boolean isFainted() {
        return fainted || (!stats.isEmpty() && stats.get(0).value <= 0);
    }
}