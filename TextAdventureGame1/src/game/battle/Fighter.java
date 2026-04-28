package game.battle;

import java.util.ArrayList;
import java.util.Random;

public class Fighter {
    public String name;
    public ArrayList<Type> types;
    public ArrayList<Move> moveset;
    public ArrayList<Stat> stats;
    public ArrayList<Type> weak;
    public ArrayList<Type> immune;
    public ArrayList<Type> resist;
    public String message;
    public String sprite;
    public String back_sprite;
    public boolean fainted = false; // ✅ track faint state

    public Fighter(String name, ArrayList<Type> types, ArrayList<Stat> stats,
                   ArrayList<Type> weak, ArrayList<Type> resist, ArrayList<Type> immune,
                   String sprite, String back_sprite) {
        this.name       = name;
        this.types      = types;
        this.stats      = stats;
        this.moveset    = new ArrayList<>();
        this.weak       = weak;
        this.resist     = resist;
        this.immune     = immune;
        this.message    = "";
        this.sprite     = sprite;
        this.back_sprite = back_sprite;
    }

    // ✅ Only this one addMove - no duplicates
    public void addMove(Move move) {
        this.moveset.add(move);
    }

    public void useMove(Fighter target, int id) {
        Move move = this.moveset.get(id);
        if (move.damage > 0) {
            target.stats.get(0).value = Math.max(0,
                    target.stats.get(0).value - damageCalc(move, this, target));
            if (target.stats.get(0).value <= 0) {
                target.fainted = true;
            }
            if (target.weak.contains(move.type) && !target.resist.contains(move.type)) {
                this.message = "It was super effective!";
            } else if (target.resist.contains(move.type)) {
                this.message = "It was not very effective.";
            } else if (target.immune.contains(move.type)) {
                this.message = "It did not affect " + target.name + ".";
            }
        }
        for (int i = 0; i < target.stats.size(); i++) {
            if (move.stat_changes.get(i) < 0) {
                this.message = target.name + "'s " + target.stats.get(i).name + " was lowered!";
                target.stats.get(i).value += target.stats.get(i).value * move.stat_changes.get(i);
            } else if (move.stat_changes.get(i) > 0) {
                if (i == 0 && this.stats.get(i).value == this.stats.get(i).base) {
                    this.message = this.name + "'s " + this.stats.get(i).name + " is full!";
                } else {
                    this.message = this.name + "'s " + this.stats.get(i).name + " was raised!";
                    this.stats.get(i).value += this.stats.get(i).value * move.stat_changes.get(i);
                }
            }
        }
    }

    private double damageCalc(Move move, Fighter user, Fighter opp) {
        double damage = (22 * move.damage * (user.stats.get(1).value / opp.stats.get(2).value)) / 50 + 2;
        if (user.types.contains(move.type)) damage *= 1.5;
        if (opp.weak.contains(move.type))   damage *= 2;
        if (opp.resist.contains(move.type)) damage *= 0.5;
        if (opp.immune.contains(move.type)) damage = 0;
        return damage;
    }

    public boolean isFainted() {
        return stats.get(0).value <= 0;
    }

    // ✅ Check if all fighters in a team are fainted
    public static boolean allFainted(ArrayList<Fighter> team, Fighter active) {
        if (!active.isFainted()) return false;
        for (Fighter f : team) {
            if (!f.isFainted()) return false;
        }
        return true;
    }

    public int chooseMove(Fighter target) {
        ArrayList<Double> scores = new ArrayList<>();
        scores.add(100.0); scores.add(100.0);
        scores.add(100.0); scores.add(100.0);

        for (int i = 0; i < 4; i++) {
            if (this.moveset.get(i).damage == 0) {
                for (int j = 0; j < target.stats.size(); j++) {
                    if (this.moveset.get(i).stat_changes.get(j) < 0) {
                        if (target.stats.get(j).value >= this.stats.get(j).value
                                && this.stats.get(j).name.equals("Speed"))
                            scores.set(i, scores.get(i) + 20);
                        else if (target.stats.get(j).name.equals("Defense")
                                && target.stats.get(j).value >= this.stats.get(1).value)
                            scores.set(i, scores.get(i) + 20);
                        else if (target.stats.get(j).name.equals("Attack")
                                && target.stats.get(j).value >= this.stats.get(2).value)
                            scores.set(i, scores.get(i) + 20);
                    } else if (this.moveset.get(i).stat_changes.get(j) > 0) {
                        if (target.stats.get(j).value >= this.stats.get(j).value
                                && this.stats.get(j).name.equals("Speed"))
                            scores.set(i, scores.get(i) + 20);
                        else if (this.stats.get(j).name.equals("Attack")
                                && this.stats.get(j).value <= target.stats.get(2).value)
                            scores.set(i, scores.get(i) + 20);
                        else if (target.stats.get(j).name.equals("Defense")
                                && this.stats.get(j).value <= target.stats.get(1).value)
                            scores.set(i, scores.get(i) + 20);
                        else if (this.stats.get(j).name.equals("Health")
                                && this.stats.get(j).value <= 50)
                            scores.set(i, scores.get(i) + 75);
                    }
                }
                if (this.stats.get(0).value <= 25)
                    scores.set(i, scores.get(i) - 50);
            } else {
                if (damageCalc(this.moveset.get(i), this, target) >= target.stats.get(0).value)
                    scores.set(i, scores.get(i) + 100);
                if (target.weak.contains(this.moveset.get(i).type))
                    scores.set(i, scores.get(i) + 75);
                if (target.resist.contains(this.moveset.get(i).type))
                    scores.set(i, scores.get(i) - 75);
                if (target.immune.contains(this.moveset.get(i).type))
                    scores.set(i, scores.get(i) - 100);
            }
        }

        double tot = 0;
        for (Double s : scores) tot += s;
        for (int k = 0; k < scores.size(); k++)
            scores.set(k, scores.get(k) / tot);

        Random rand = new Random();
        double roll = rand.nextDouble();
        if (roll <= scores.get(0)) return 0;
        else if (roll <= scores.get(0) + scores.get(1)) return 1;
        else if (roll <= scores.get(0) + scores.get(1) + scores.get(2)) return 2;
        else return 3;
    }
}