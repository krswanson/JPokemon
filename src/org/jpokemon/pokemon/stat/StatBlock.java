package org.jpokemon.pokemon.stat;

import java.util.List;

import org.jpokemon.JPokemonConstants;
import org.jpokemon.pokemon.ConditionEffect;
import org.jpokemon.pokemon.EffortValue;
import org.jpokemon.pokemon.PokemonInfo;
import org.zachtaylor.jnodalxml.XMLException;
import org.zachtaylor.jnodalxml.XMLNode;

public class StatBlock {
  public static final String XML_NODE_NAME = "stats";

  public StatBlock(PokemonInfo info) {
    _data = new Stat[StatType.values().length];

    _data[0] = new Health();
    for (int i = 1; i < _data.length; i++)
      _data[i] = new Stat();

    rebase(info);
  }

  /**
   * Gets the StatType specified
   * 
   * @param type StatType to fetch
   * @return Stat specified by the type
   */
  public Stat get(StatType type) {
    return _data[type.ordinal()];
  }

  /**
   * Resets each stat
   */
  public void reset() {
    for (Stat s : _data)
      s.reset();
  }

  /**
   * Applies a new level to each Stat
   * 
   * @param level New level of the Pokemon which has these stats
   */
  public void level(int level) {
    for (Stat s : _data)
      s.level(level);
  }

  /**
   * Get the total number of unused Stat Points
   * 
   * @return Free stat point count
   */
  public int points() {
    return _points;
  }

  /**
   * Set the total number of unused Stat Points
   * 
   * @param p Amount of points
   */
  public void points(int p) {
    _points = p;
  }

  public void usePoints(StatType st, int amount) {
    if (points() < amount)
      throw new IllegalStateException("No points available");

    Stat stat = get(st);
    int consumable = Math.min(JPokemonConstants.STAT_POINTS_INDIVIDUAL_MAX - stat.points(), amount);
    if (consumable <= 0)
      return;

    points(points() - amount);
    stat.points(stat.points() + amount);
  }

  /**
   * Sets up each stat to use the value from the PokemonBase specified
   * 
   * @param info PokemonBase which has the new base values for each stat
   */
  public void rebase(PokemonInfo info) {
    // This is not polymorphic, because that would make the database ugly

    _data[StatType.HEALTH.ordinal()].base(info.getHealth());
    _data[StatType.ATTACK.ordinal()].base(info.getAttack());
    _data[StatType.SPECATTACK.ordinal()].base(info.getSpecattack());
    _data[StatType.DEFENSE.ordinal()].base(info.getDefense());
    _data[StatType.SPECDEFENSE.ordinal()].base(info.getSpecdefense());
    _data[StatType.SPEED.ordinal()].base(info.getSpeed());
  }

  public void addEV(List<EffortValue> evs) {
    for (EffortValue ev : evs) {
      Stat stat = get(StatType.valueOf(ev.getStat()));
      int consumable = ev.getAmount();

      if (_evTotal + ev.getAmount() > JPokemonConstants.EFFORT_VALUE_UNIVERSAL_MAX) {
        consumable = JPokemonConstants.EFFORT_VALUE_UNIVERSAL_MAX - _evTotal;
      }
      else if (stat.ev() + ev.getAmount() > JPokemonConstants.EFFORT_VALUE_INDIVIDUAL_MAX) {
        consumable = JPokemonConstants.EFFORT_VALUE_INDIVIDUAL_MAX - stat.ev();
      }

      _evTotal += consumable;
      stat.ev(consumable);
    }
  }

  /**
   * Applies a stat penalty, as a result of a condition issue
   * 
   * @param i The issue which applies a stat penalty
   */
  public void addConditionEffect(ConditionEffect i) {
    // Also not polymorhpic, because condition issues don't map a stat

    if (i == ConditionEffect.BURN) {
      _burn = true;
      _data[StatType.ATTACK.ordinal()].modify(1.0 / 2.0);
    }
    else if (i == ConditionEffect.PARALYZE) {
      _paralyze = true;
      _data[StatType.SPEED.ordinal()].modify(1.0 / 4.0);
    }
  }

  /**
   * Removes the previously added stat penalty of a condition issue
   * 
   * @param i The issue to reset the effects of
   */
  public void removeConditionEffect(ConditionEffect i) {
    if (i == ConditionEffect.BURN && _burn) {
      _burn = false;
      _data[StatType.ATTACK.ordinal()].modify(1);
    }
    else if (i == ConditionEffect.PARALYZE && _paralyze) {
      _paralyze = false;
      _data[StatType.SPEED.ordinal()].modify(1);
    }
  }

  public XMLNode toXML() {
    XMLNode node = new XMLNode(XML_NODE_NAME);

    node.setAttribute("points", _points);
    node.setAttribute("evtotal", _evTotal);

    for (StatType st : StatType.values()) {
      XMLNode child = get(st).toXML();
      child.setAttribute("type", st.toString());
      node.addChild(child);
    }

    return node;
  }

  public void loadXML(XMLNode node) {
    if (!XML_NODE_NAME.equals(node.getName()))
      throw new XMLException("Cannot read node");

    _points = node.getIntAttribute("points");
    _evTotal = node.getIntAttribute("evtotal");

    for (XMLNode childNode : node.getChildren(Stat.XML_NODE_NAME)) {
      Stat s = get(StatType.valueOf(childNode.getAttribute("type")));
      s.loadXML(childNode);
    }
  }

  private Stat[] _data;
  private int _points, _evTotal;
  private boolean _burn, _paralyze;
}