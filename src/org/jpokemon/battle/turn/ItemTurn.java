package org.jpokemon.battle.turn;

import org.jpokemon.battle.slot.Slot;
import org.jpokemon.item.Item;
import org.jpokemon.item.ItemType;
import org.jpokemon.pokemon.Pokemon;
import org.jpokemon.trainer.WildTrainer;

public class ItemTurn extends Turn {
  public ItemTurn(Slot user, Slot target, Item item, int targetIndex) {
    super(user, target);

    _targetIndex = targetIndex;
    _item = item;

    addMessage(slot().trainer().name() + " used " + item.name());
  }

  @Override
  protected void doExecute() {
    Pokemon target = target().party().get(_targetIndex);

    if (_item.type() == ItemType.MACHINE)
      addMessage("Machines aren't allowed in battle!");
    else if (_item.type() == ItemType.STONE)
      addMessage("Stones aren't allowed in battle!");
    else if (_item.type() == ItemType.POTION)
      _item.effect(target);
    else if (_item.type() == ItemType.BALL) {
      if (!(target().trainer() instanceof WildTrainer))
        addMessage("Cannot use a ball against " + target.name() + "!");
      else if (_item.effect(target)) {
        if (!slot().trainer().add(target))
          addMessage("No room for " + target.name());
        else {
          target().party().remove(target);
          addMessage(target.name() + " was caught!");
        }
      }
      else
        addMessage(target.name() + "broke free!");
    }
  }

  @Override
  public boolean reAdd() {
    return false;
  }

  @Override
  public int compareTo(Turn turn) {
    if (turn._needSwap) {
      if (_needSwap)
        return 0;

      return 1;
    }

    if (turn instanceof AttackTurn)
      return -1;
    if (turn instanceof ItemTurn)
      return 0;
    return 1;
  }

  private int _targetIndex;
  private Item _item;
}