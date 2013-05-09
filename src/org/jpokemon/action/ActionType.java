package org.jpokemon.action;

public enum ActionType {
  SPEECH, EVENT, ITEM, TRANSPORT, POKEMON, BATTLE, UPGRADE;

  public static ActionType valueOf(int t) {
    return values()[t];
  }
}