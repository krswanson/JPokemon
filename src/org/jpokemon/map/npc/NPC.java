package org.jpokemon.map.npc;

import java.util.ArrayList;
import java.util.List;

import org.jpokemon.JPokemonConstants;
import org.jpokemon.action.ActionSet;
import org.jpokemon.trainer.Player;

import com.njkremer.Sqlite.DataConnectionException;
import com.njkremer.Sqlite.DataConnectionManager;
import com.njkremer.Sqlite.SqlStatement;

public class NPC {
  public static NPC get(int number) {
    DataConnectionManager.init(JPokemonConstants.DATABASE_PATH);

    try {
      List<NPC> query = SqlStatement.select(NPC.class).where("number").eq(number).getList();

      if (query.size() > 0)
        return query.get(0);
    } catch (DataConnectionException e) {
      e.printStackTrace();
    }

    return null;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int n) {
    number = n;
  }

  public NPCType getType() {
    return npctype;
  }

  public int getTypeAsInt() {
    return type;
  }

  public void setType(int t) {
    type = t;
    npctype = NPCType.get(t);
  }

  public String getName() {
    return name.replace("{typename}", getType().getName());
  }

  public String getNameRaw() {
    return name;
  }

  public void setName(String n) {
    name = n;
  }

  public String getIcon() {
    return getType().getIcon();
  }

  public void addActionSet(ActionSet actionset) {
    _actions.add(actionset);
  }

  public ActionSet actionset(String option) {
    for (ActionSet as : _actions) {
      if (option.equals(as.getOption())) {
        return as;
      }
    }

    return null;
  }

  public List<String> getOptionsForPlayer(Player player) {
    List<String> options = new ArrayList<String>();

    for (ActionSet actionset : _actions) {
      if (actionset.isOkay(player)) {
        options.add(actionset.getOption());
      }
    }

    return options;
  }

  public String toString() {
    return "NPC#" + number + ": " + getName();
  }

  private String name;
  private int number, type;
  private NPCType npctype;
  private List<ActionSet> _actions = new ArrayList<ActionSet>();
}