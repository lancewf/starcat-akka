package org.starcat.structures

abstract class Bond(val from:Entity, val to:Entity, strength:Int = 0) extends Entity {
  def equals(bond:Bond):Boolean = {
    return (super.equals(bond) 
    && from.equals(bond.from)
    && to.equals(bond.to))
  }
}