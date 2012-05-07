package jpkmn.pokemon.move;

import java.util.ArrayList;
import java.util.List;

import lib.BonusEffectBase;
import lib.MoveBase;

import jpkmn.battle.Target;
import jpkmn.pokemon.Pokemon;
import jpkmn.pokemon.Type;

public class Move {
  public final Pokemon pkmn;

  /**
   * Creates a new move of the specified number
   * 
   * @param num The number of the move
   * @param user The user of the move
   */
  public Move(int num, Pokemon user) {
    number = num;
    pkmn = user;
    enabled = true;

    MoveBase base = MoveBase.getBaseForNumber(number);
    name = base.getName();
    power = base.getPower();
    pp = ppmax = base.getPp();
    accuracy = base.getAccuracy();
    type = Type.valueOf(base.getType());
    style = MoveStyle.valueOf(base.getStyle());

    setBonusEffects();
  }

  public String name() {
    return name;
  }

  public int number() {
    return number;
  }

  public int power() {
    return power;
  }

  public MoveStyle style() {
    return style;
  }

  public Type type() {
    return type;
  }

  /**
   * Tells whether it is valid to use this move. This method will reduce PP.
   * Note that it is not appropriate to call this method on repeat-style moves,
   * or multi-turn moves.
   * 
   * @return True if the move can be performed this turn
   */
  public boolean use() {
    return enabled = enabled && pp-- > 0;
  }

  /**
   * Reset base attributes for a move
   */
  public void restore() {
    pp = ppmax;
    enabled = true;
  }

  /**
   * Determine whether a move is normal, super, or not very effective
   * 
   * @param p The pokemon targeted by the move
   * @return How effect the move is
   */
  public double effectiveness(Pokemon p) {
    return type.effectiveness(p);
  }

  /**
   * Tells the Same-Type-Attack-Bonus advantage. 1.5 is true, 1 if false
   * 
   * @return the STAB advantage
   */
  public double STAB() {
    return (type == pkmn.type1() || type == pkmn.type2()) ? 1.5 : 1.0;
  }

  /**
   * Figures out whether this move hits the target. Each time hits is called,
   * it is random. Therefore, do not call it multiple times per attempted
   * attack. This method only computes the probability of hitting, but does not
   * effect pp.
   * 
   * @param target Target pokemon
   * @return True if the move hits
   */
  public boolean hits(Pokemon target) {

    // Move # 141 (Swift) never misses
    if (number == 141)
      return true;

    if (style == MoveStyle.OHKO) {
      int levelDiff = pkmn.level() - target.level();

      if (levelDiff >= 0 && (levelDiff + 30.0) / 100.0 > Math.random())
        return true;
      else
        return false;
    }
    else
      return accuracy >= Math.random();
  }

  @Override
  public String toString() {
    return name + " (" + pp + "/" + ppmax + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Move)) return false;

    return number == ((Move) o).number;
  }

  /**
   * Loads and sets all the bonus effects for a move
   */
  private void setBonusEffects() {
    BonusEffect current;
    be = new ArrayList<BonusEffect>();

    for (BonusEffectBase base : BonusEffectBase.getBasesForMoveNumber(number)) {
      // Get Bonus Effect type
      current = BonusEffect.valueOf(base.getType());
      // Add all attributes, including unused
      current.target = Target.valueOf(base.getTarget());
      current.chance = base.getChance();
      current.percent = base.getPercent();
      current.power = base.getPower();
      // Add to moves list of effects
      be.add(current);
    }
  }

  private String name;
  private int number, power, pp, ppmax;
  private double accuracy;
  private boolean enabled = true;
  private Type type;
  private MoveStyle style;
  private List<BonusEffect> be;
}