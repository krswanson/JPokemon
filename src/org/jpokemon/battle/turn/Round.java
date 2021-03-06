package org.jpokemon.battle.turn;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.jpokemon.battle.Battle;
import org.jpokemon.battle.Reward;
import org.jpokemon.battle.slot.Slot;

public class Round {
  public Round(Battle b) {
    _battle = b;
  }

  public int size() {
    return _turns.size();
  }

  public void add(Turn t) {
    _turns.put(t.slot(), t);
  }

  public void execute() {
    Queue<Turn> queue = new PriorityQueue<Turn>();

    // Setup rivals + turn queue
    for (Slot a : _battle) {
      for (Slot b : _battle) {
        if (a.team() != b.team()) {
          a.addRival(b);
        }
      }

      queue.add(_turns.get(a));
    }

    // MADNESS
    while (!queue.isEmpty()) {
      Turn turn = queue.remove();

      if (_turns.remove(turn.slot()) == null) {
        continue;
      }

      turn.execute();
      notifyAllTrainers(turn.getMessages());

      verifyTurnList();

      if (turn.reAdd()) {
        _battle.addTurn(turn);
      }
    }

    applyEndOfRoundEffects();
  }

  private void verifyTurnList() {
    Slot slotWithNoPokemon = null, slotWithFaintedLeader = null;

    for (Slot s : _battle) {
      if (s.party().awake() == 0) {
        slotWithNoPokemon = s;
        break;
      }
      else if (!s.leader().awake()) {
        slotWithFaintedLeader = s;
        break;
      }
    }

    if (slotWithNoPokemon != null) {
      rewardFrom(slotWithNoPokemon);

      _turns.remove(slotWithNoPokemon);
      _battle.remove(slotWithNoPokemon);
    }
    else if (slotWithFaintedLeader != null) {
      rewardFrom(slotWithFaintedLeader);

      if (_turns.get(slotWithFaintedLeader) != null) {
        _turns.get(slotWithFaintedLeader).forceSwap();
      }
    }
  }

  private void applyEndOfRoundEffects() {
    for (Slot slot : _battle) {
      // Condition effects
      slot.leader().applyConditionEffects();
      notifyAllTrainers(slot.leader().lastConditionMessage());

      // Slot effects
      String[] messages = slot.applySlotEffects();
      notifyAllTrainers(messages);
    }
  }

  private void notifyAllTrainers(String[] message) {
    for (Slot s : _battle)
      s.trainer().notify(message);
  }

  private void rewardFrom(Slot slot) {
    Reward reward = new Reward(slot);

    for (Slot s : _battle) {
      if (s == slot) {
        continue;
      }

      reward.apply(s);
    }
  }

  private Battle _battle;
  private Map<Slot, Turn> _turns = new HashMap<Slot, Turn>();
}