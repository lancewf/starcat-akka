package org.finfrock.starcat.workspace

import org.finfrock.starcat.structures.Entity
import scala.util.Random

class WorkspaceIterator(val entities: List[Entity]) extends Iterator[Entity] {
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

  def next(): Entity = {
    var index = rand.nextInt() % beenSelected.length
    while (beenSelected(index)) {
      index = rand.nextInt() % beenSelected.length
    }

    val selectedEntity = entities(index)
    beenSelected(index) = true;
    selectedCount += 1

    return selectedEntity;
  }

  def remove() {}
}