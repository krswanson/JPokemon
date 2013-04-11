package org.jpokemon.service;

import org.jpokemon.pokemon.Pokemon;
import org.jpokemon.pokemon.stat.StatType;
import org.jpokemon.trainer.Player;
import org.jpokemon.trainer.PlayerFactory;
import org.jpokemon.trainer.TrainerState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayerService {
  public static JSONObject pull(int playerID) {
    Player p = PlayerFactory.get(playerID);

    JSONObject response = new JSONObject();
    try {
      response.put("state", p.state().toString());
      response.put("player", p.toJSON(null));

      if (p.state() == TrainerState.BATTLE)
        response.put("battle", BattleService.info(playerID));
      else if (p.state() == TrainerState.UPGRADE)
        response.put("upgrade", p.toJSON(p.state()));

    } catch (Exception e) {
      e.printStackTrace();
      // throw new ServiceException(e.getMessage());
    }

    return response;
  }

  public static void load(String name) throws ServiceException {
    try {
      Player p = PlayerFactory.load(name);

      p.state(TrainerState.UPGRADE);
      p.notify("Foo", "Bar");
      p.notify("Hello world");
    } catch (LoadException e) {
      e.printStackTrace();
      throw new ServiceException(e.getMessage());
    }
  }

  public static void save(int playerID) throws ServiceException {
    PlayerFactory.save(playerID);
  }

  public static void party(JSONObject request) {
    try {
      Player player = PlayerFactory.get(request.getInt("id"));

      if (player.state() == TrainerState.OVERWORLD) {
        player.state(TrainerState.UPGRADE);
      }
      else if (player.state() == TrainerState.UPGRADE) {
        if (request.get("stats") == JSONObject.NULL) {
          player.state(TrainerState.OVERWORLD);
          return;
        }

        Pokemon pokemon = player.party().get(request.getInt("index"));

        JSONArray stats = request.getJSONArray("stats");
        for (int i = 0; i < stats.length(); i++) {
          StatType s = StatType.valueOf(stats.getJSONObject(i).getString("stat"));
          pokemon.statPoints(s, stats.getJSONObject(i).getInt("amount"));
        }
      }
      else
        ; // TODO throw error

    } catch (JSONException e) {
    }
  }

}