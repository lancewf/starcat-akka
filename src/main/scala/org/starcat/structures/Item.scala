package org.starcat.structures

abstract class Item extends Entity {

  private var bonds = Set[Bond]()
  
  def getBonds = bonds.toList
  
  def addBond(bond:Bond){
    bonds += bond
  }
}