package org.jpokemon.trainer;

import java.util.ArrayList;
import java.util.List;

import org.zachtaylor.jnodalxml.XMLException;
import org.zachtaylor.jnodalxml.XMLNode;

/**
 * Stores a player's history and useful things about what they have done
 */
public class Record {
  public static final String XML_NODE_NAME = "record";

  public void setRivalName(String rivalName) {
    _rivalName = rivalName;
  }

  public String getRivalName() {
    return _rivalName;
  }

  public void setStarterPokemon(String p) {
    _pokemon = p;
  }

  public String getStarterPokemon() {
    return _pokemon;
  }

  /**
   * Sets the specified event id
   * 
   * @param id Event number to record
   */
  public void putEvent(int id) {
    if (id < 1)
      throw new IllegalArgumentException("Out of bounds event: " + id);
    if (_events.contains(id))
      throw new IllegalArgumentException("Duplicate put for event: " + id);

    _events.add(id);
  }

  /**
   * Gets the status of an event
   * 
   * @param id Event number to look up
   * @return True if the event has been completed
   */
  public boolean getEvent(int id) {
    if (id < 1)
      throw new IllegalArgumentException("Out of bounds event: " + id);

    return _events.contains(id);
  }

  /**
   * Sets the specified trainer id
   * 
   * @param id Trainer number to record
   */
  public void putTrainer(String id) {
    if (_trainers.contains(id))
      throw new IllegalArgumentException("Duplicate put for trainer " + id);

    _trainers.add(id);
  }

  /**
   * Gets whether a Trainer has been fought before
   * 
   * @param id Trainer number to look up
   * @return True if the Trainer has been defeated
   */
  public boolean getTrainer(String id) {
    return _trainers.contains(id);
  }

  public String replaceMacros(String s, String playerName) {
    if (_rivalName != null)
      s = s.replaceAll("\\{rival\\}", _rivalName);

    if (playerName != null)
      s = s.replaceAll("\\{player\\}", playerName);

    if (_pokemon != null)
      s = s.replaceAll("\\{starter\\}", _pokemon);

    return s;
  }

  public XMLNode toXML() {
    XMLNode node = new XMLNode(XML_NODE_NAME);

    if (_rivalName != null) {
      node.setAttribute("rival", _rivalName);
    }

    if (_pokemon != null) {
      node.setAttribute("starter", _pokemon);
    }

    XMLNode eventNode = new XMLNode("events");
    eventNode.setValue(_events.toString());
    node.addChild(eventNode);

    XMLNode trainerNode = new XMLNode("trainers");
    trainerNode.setValue(_trainers.toString());
    node.addChild(trainerNode);

    return node;
  }

  public void loadXML(XMLNode node) {
    if (!XML_NODE_NAME.equals(node.getName()))
      throw new XMLException("Cannot read node");

    if (node.hasAttribute("rival")) {
      _rivalName = node.getAttribute("rival");
    }

    if (node.hasAttribute("starter")) {
      _pokemon = node.getAttribute("starter");
    }

    for (String value : node.getChildren("events").get(0).getValue().replace('[', ' ').replace(']', ' ').trim().split(",")) {
      if (value.isEmpty())
        continue;

      putEvent(Integer.parseInt(value));
    }

    for (String value : node.getChildren("trainers").get(0).getValue().replace('[', ' ').replace(']', ' ').trim().split(",")) {
      if (value.isEmpty())
        continue;

      putTrainer(value);
    }
  }

  private String _rivalName, _pokemon;
  private List<Integer> _events = new ArrayList<Integer>();
  private List<String> _trainers = new ArrayList<String>();
}