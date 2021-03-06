package org.jpokemon.pokemon.stat;

import org.jpokemon.JPokemonConstants;
import org.zachtaylor.jnodalxml.XMLException;
import org.zachtaylor.jnodalxml.XMLNode;

public class Stat {
  public static final String XML_NODE_NAME = "stat";

  public Stat() {
    _level = 1;
    _modifier = 1;
    _iv = (int) (Math.random() * JPokemonConstants.INDIVIDUAL_VALUE_RANGE_CAP);
  }

  public int cur() {
    return _cur;
  }

  public int max() {
    return _max;
  }

  public int ev() {
    return _ev + _evPending;
  }

  public void ev(int val) {
    if (!JPokemonConstants.MEASURE_EFFORT_VALUE_REALTIME) {
      _evPending += val;
    }
    else {
      _ev += val;
      computeMax();
      computeCur();
    }
  }

  public int iv() {
    return _iv;
  }

  public void iv(int val) {
    _iv = val;

    computeMax();
    computeCur();
  }

  public int points() {
    return _pts;
  }

  public void points(int p) {
    _pts = p;
    reset();
  }

  public void level(int l) {
    _level = l;

    if (!JPokemonConstants.MEASURE_EFFORT_VALUE_REALTIME) {
      _ev += _evPending;
      _evPending = 0;
    }

    reset();
  }

  public void base(int b) {
    _base = b;
    reset();
  }

  public void modify(double m) {
    _modifier = m;

    computeCur();
  }

  public void effect(int power) {
    _delta += power;

    if (Math.abs(_delta) > JPokemonConstants.STAT_CHANGE_MAX_DELTA)
      _delta = (int) Math.copySign(JPokemonConstants.STAT_CHANGE_MAX_DELTA, _delta);

    computeCur();
  }

  public void reset() {
    _delta = 0;

    computeMax();
    computeCur();
  }

  public XMLNode toXML() {
    XMLNode myNode = new XMLNode(XML_NODE_NAME);

    myNode.setAttribute("cur", _cur);
    myNode.setAttribute("max", _max);
    myNode.setAttribute("points", _pts);
    myNode.setAttribute("ev", _ev);
    myNode.setAttribute("iv", _iv);
    myNode.setAttribute("ev_pending", _evPending);
    myNode.setSelfClosing(true);

    return myNode;
  }

  public void loadXML(XMLNode node) {
    if (!XML_NODE_NAME.equals(node.getName()))
      throw new XMLException("Cannot read node");

    _cur = node.getIntAttribute("cur");
    _max = node.getIntAttribute("max");
    _pts = node.getIntAttribute("points");
    _ev = node.getIntAttribute("ev");
    _iv = node.getIntAttribute("iv");
    _evPending = node.getIntAttribute("ev_pending");
  }

  private void computeMax() {
    double val = (JPokemonConstants.STAT_MAX_VALUE_WEIGHT_BASE * _base);
    val += (JPokemonConstants.STAT_MAX_VALUE_WEIGHT_IV * _iv);
    val += (JPokemonConstants.STAT_MAX_VALUE_WEIGHT_EV * _ev);
    val += (JPokemonConstants.STAT_MAX_VALUE_WEIGHT_POINTS * _pts);
    val += 100;
    val /= 100;
    val *= _level;
    val += 10;

    _max = (int) val;
  }

  private void computeCur() {
    if (_delta > 0)
      _cur = (int) ((_max * ((2.0 + _delta) / 2)));
    else if (_delta < 0)
      _cur = (int) (_max * 2.0 / (2 + -_delta));
    else
      _cur = _max;

    _cur = (int) Math.max(_cur * _modifier, 1);
  }

  protected double _modifier;
  protected int _delta, _cur, _max, _base, _pts, _level, _ev, _evPending, _iv;
}