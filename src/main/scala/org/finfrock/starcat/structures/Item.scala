package org.finfrock.starcat.structures

/**
 * This class represents items other than WorkspaceStructure objects in the
 * Workspace. Because we are trying to create an architecture that is
 * domain-neutral, we should probably strive to have this be as general as
 * possible, and allow Description objects to define it. If we follow the
 * Copycat model, it seems that anything that would like to be an object, should
 * be instance-linked to the "object-category" node in the Slipnet
 */
trait Item extends Entity {

  private var bonds = Set[Bond]()
  
  def getBonds = bonds.toList
  
  def addBond(bond:Bond){
    bonds += bond
  }
}