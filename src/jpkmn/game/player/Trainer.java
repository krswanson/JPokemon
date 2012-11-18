package jpkmn.game.player;

import jpkmn.exceptions.LoadException;
import jpkmn.game.base.AIInfo;
import jpkmn.game.pokemon.Pokemon;

import org.jpokemon.JPokemonConstants;
import org.jpokemon.pokemon.storage.PokemonStorageUnit;

public class Trainer implements PokemonTrainer, JPokemonConstants {
  public Trainer() {
    _id = -1;
    _name = "Mock Player";
    _type = TrainerType.WILD;
    _party = new PokemonStorageUnit(PARTYSIZE);
  }

  public Trainer(int ai_number) throws LoadException {
    this();
    AIInfo info = AIInfo.get(ai_number);

    _id = ai_number;
    _name = info.getName();
    _cash = info.getCash();
    _type = TrainerType.valueOf(info.getType());

    for (String entry : info.getPokemon())
      add(Pokemon.load(entry));
  }

  public int id() {
    return _id;
  }

  public String name() {
    return _name;
  }

  public void name(String name) {
    _name = name;
  }

  public int cash() {
    return _cash;
  }

  public void cash(int cash) {
    _cash = cash;
  }

  public TrainerType type() {
    return _type;
  }

  public PokemonStorageUnit party() {
    return _party;
  }

  public boolean add(Pokemon p) {
    return party().add(p);
  }

  public void notify(String... message) {
    return;
  }

  private String _name;
  private int _id, _cash;
  private TrainerType _type;
  private PokemonStorageUnit _party;
}