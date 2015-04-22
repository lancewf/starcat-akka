package org.finfrock.starcat.util

import scala.util.Random

class RandomIterator[T](val entities: List[T]) extends Iterator[T] {
  private val beenSelected = Array.fill[Boolean](entities.size)(false)
  private var selectedCount: Int = 0
  private val rand = new Random()

  def hasNext(): Boolean = {
    if (selectedCount < beenSelected.length) {
      return true;
    } else {
      return false;
    }
  } 

  def next(): T = {
    var index = rand.nextInt() % beenSelected.length
    while (beenSelected(index)) {
      index = rand.nextInt() % beenSelected.length
    }

    val selectedEntity = entities(index)
    beenSelected(index) = true
    selectedCount += 1

    return selectedEntity
  }

  def remove() {}
}