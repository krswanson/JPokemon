package org.jpokemon.map.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jpokemon.JPokemonConstants;
import com.njkremer.Sqlite.DataConnectionException;
import com.njkremer.Sqlite.DataConnectionManager;
import com.njkremer.Sqlite.SqlStatement;

/**
 * A type of NPC (Bug Catcher, Lass, etc). NPCType will distinguish the way a
 * NPCs name is displayed, and they way they appear.
 */
public class NPCType {
  private int number;
  private String name, icon;

  private static Map<Integer, NPCType> cache = new HashMap<Integer, NPCType>();

  public static NPCType get(int number) {
    DataConnectionManager.init(JPokemonConstants.DATABASE_PATH);

    if (cache.get(number) == null) {
      try {
        List<NPCType> info = SqlStatement.select(NPCType.class).where("number").eq(number).getList();

        if (info.size() > 0)
          cache.put(number, info.get(0));

      } catch (DataConnectionException e) {
        e.printStackTrace();
      }
    }

    return cache.get(number);
  }

  public String toString() {
    return "NPCType#" + number + ": " + getName();
  }

  //@preformat
  public int getNumber() {return number;} public void setNumber(int n) {number = n;}
  public String getName() {return name;} public void setName(String n) {name = n;}
  public String getIcon() {return icon;} public void setIcon(String i) {icon = i;}
  //@format
}