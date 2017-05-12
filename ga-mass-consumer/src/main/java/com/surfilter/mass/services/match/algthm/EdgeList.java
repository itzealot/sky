package com.surfilter.mass.services.match.algthm;

import java.io.Serializable;


/**
 * Simple interface for mapping bytes to States.
 */
interface EdgeList<T> extends Serializable{
  State<T> get(byte ch);

  void put(byte ch, State<T> state);

  byte[] keys();
}
