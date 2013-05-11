package org.jpokemon;

import org.jpokemon.item.Bag;
import org.jpokemon.item.Item;
import org.jpokemon.pokedex.Pokedex;
import org.jpokemon.pokemon.Pokemon;
import org.jpokemon.pokemon.move.Move;
import org.jpokemon.pokemon.stat.Stat;
import org.jpokemon.pokemon.stat.StatType;
import org.jpokemon.pokemon.storage.PokemonStorageBlock;
import org.jpokemon.pokemon.storage.PokemonStorageUnit;
import org.jpokemon.trainer.Player;
import org.jpokemon.trainer.Record;

public abstract class AbstractJPokemonVisitor implements JPokemonVisitor {
  @Override
  public Object data() {
    return data;
  }

  public void setData(Object object) {
    data = object;
  }

  @Override
  public void visit_player(Player player) {
    // TODO Auto-generated method stub

    visit_record(player.record());

    visit_pokedex(player.pokedex());

    visit_bag(player.bag());

    visit_storage_block(player.getAllPokemon());
  }

  @Override
  public void visit_record(Record record) {
  }

  @Override
  public void visit_pokedex(Pokedex pokedex) {
  }

  @Override
  public void visit_bag(Bag bag) {
    for (Item item : bag) {
      visit_item(lastItem = item);
    }
  }

  @Override
  public void visit_item(Item item) {
  }

  protected Item last_visited_item() {
    return lastItem;
  }

  @Override
  public void visit_storage_block(PokemonStorageBlock block) {
    PokemonStorageUnit unit;

    for (int i = 0; i < JPokemonConstants.PLAYER_STORAGE_UNIT_COUNT; i++) {
      unit = lastUnit = block.get(i);

      if (i < 1) {
        visit_party(unit);
      }
      else {
        visit_storage_unit(unit);
      }
    }
  }

  @Override
  public void visit_party(PokemonStorageUnit unit) {
    Pokemon pokemon;

    for (int i = 0; i < unit.size(); i++) {
      pokemon = lastPokemon = unit.get(i);

      if (i < 1) {
        visit_party_leader(pokemon);
      }
      else {
        visit_pokemon(pokemon);
      }
    }
  }

  @Override
  public void visit_storage_unit(PokemonStorageUnit unit) {
    for (Pokemon pokemon : unit) {
      visit_pokemon(lastPokemon = pokemon);
    }
  }

  protected PokemonStorageUnit last_storage_unit() {
    return lastUnit;
  }

  @Override
  public void visit_party_leader(Pokemon pokemon) {
    visit_pokemon(pokemon);
  }

  @Override
  public void visit_pokemon(Pokemon pokemon) {
    for (StatType st : StatType.values()) {
      visit_stat(pokemon.getStat(lastStatType = st));
    }

    for (int moveIndex = 0; moveIndex < pokemon.moveCount(); moveIndex++) {
      visit_move(pokemon.move(moveIndex));
    }
  }

  protected Pokemon last_pokemon() {
    return lastPokemon;
  }

  public void visit_stat(Stat stat) {
  }

  protected StatType last_stat_type() {
    return lastStatType;
  }

  public void visit_move(Move move) {
  }

  private Object data;
  private Item lastItem;
  private Pokemon lastPokemon;
  private StatType lastStatType;
  private PokemonStorageUnit lastUnit;
}