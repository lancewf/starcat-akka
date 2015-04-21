package org.finfrock.starcat.slipnet

import akka.actor.ActorRef

object Link{
   def createIdentityLink(node:ActorRef, linkName:String):Link = {
    new Link(linkName, 99, node, node);
  } 
   
  private val MAXIMUM_LENGTH = 100.0
}

case class Link(val name:String, val intrinsicLength:Int, val fromSlipnetNode:ActorRef,
      val toSlipnetNode:ActorRef) {
  
  override def clone() = Link(name, intrinsicLength, fromSlipnetNode, toSlipnetNode)

  def getDegreeOfAssociation():Double = Link.MAXIMUM_LENGTH - intrinsicLength

  def isToNode(node:ActorRef):Boolean = toSlipnetNode.path == node.path

  def isFromNode(node:ActorRef):Boolean = fromSlipnetNode.path == node.path
}

class PropertyLink(name:String, intrinsicLength:Int, fromSlipnetNode:ActorRef, 
    toSlipnetNode:ActorRef) extends Link(name, intrinsicLength, fromSlipnetNode, toSlipnetNode)

class InstanceLink(name:String, intrinsicLength:Int, fromSlipnetNode:ActorRef, 
    toSlipnetNode:ActorRef) 
extends Link(name, intrinsicLength, fromSlipnetNode, toSlipnetNode)

class CategoryLink(name:String, intrinsicLength:Int, fromSlipnetNode:ActorRef, 
    toSlipnetNode:ActorRef) 
extends Link(name, intrinsicLength, fromSlipnetNode, toSlipnetNode)