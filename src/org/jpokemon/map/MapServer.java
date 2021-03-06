package org.jpokemon.map;

import org.jpokemon.activity.ActivityServer;
import org.jpokemon.map.npc.NPC;
import org.jpokemon.trainer.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapServer extends ActivityServer {
  public MapServer(Player player) {
    super(player);

    visit(player);
  }

  @Override
  public void visit(Player player) {
    Area area = Map.area(player.area());

    npcs_json = new JSONArray();
    for (NPC npc : area.npcs()) {
      visit(npc);
    }

    borders_json = new JSONArray();
    for (Border border : area.borders()) {
      visit(border);
    }

    try {
      data().put("name", area.getName());
      data().put("npcs", npcs_json);
      data().put("borders", borders_json);
      data().put("has_wild_pokemon", area.wildPokemon().size() > 0);
    } catch (JSONException e) {
    }
  }

  private void visit(NPC npc) {
    JSONObject json = new JSONObject();

    try {
      json.put("name", npc.getName());
      json.put("number", npc.getNumber());
      json.put("icon", npc.getIcon());
      json.put("options", new JSONArray());

      for (String option : npc.getOptionsForPlayer(get_calling_player())) {
        json.getJSONArray("options").put(option);
      }
    } catch (JSONException e) {
    }

    npcs_json.put(json);
  }

  private void visit(Border border) {
    JSONObject json = new JSONObject();

    Area nextArea = Map.area(border.getNext());

    try {
      json.put("name", nextArea.getName());

      String reason = border.isOkay(get_calling_player());

      if (reason == null) {
        json.put("is_okay", true);
      }
      else {
        json.put("is_okay", false);
        json.put("reason", reason);
      }
    } catch (JSONException e) {
    }

    borders_json.put(json);
  }

  private JSONArray npcs_json, borders_json;
}