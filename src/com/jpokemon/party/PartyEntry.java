package com.jpokemon.party;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import org.jpokemon.service.ImageService;
import org.json.JSONException;
import org.json.JSONObject;

import com.jpokemon.JPokemonMenuEntry;

public class PartyEntry extends JPokemonMenuEntry {
  public PartyEntry(PartyMenu parent, JSONObject data) throws JSONException {
    super(parent);

    _icon = new JLabel(ImageService.pokemon(data.getInt("number") + ""));
    _name = new JLabel(data.getString("name"));

    add(_icon);
    add(_name);

    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
  }

  public void action() {
    ((PartyMenu) parent()).navPokemon();
  }

  private JLabel _icon, _name;

  private static final long serialVersionUID = 1L;
}