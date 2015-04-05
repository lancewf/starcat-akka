package org.starcat.slipnet

object Link{
   def createIdentityLink(node:SlipnetNode):Link = {
    val name = "IndentityLink - " + node.name

    new Link(name, 99, node, node);
  } 
   
  private val MAXIMUM_LENGTH = 100.0
}

case class Link(val name:String, val intrinsicLength:Int, val fromNode:SlipnetNode,
      val toNode:SlipnetNode) {
  
  override def clone() = Link(name, intrinsicLength, fromNode, toNode)

  def getDegreeOfAssociation():Double = Link.MAXIMUM_LENGTH - intrinsicLength

  def isToNode(node:SlipnetNode):Boolean = toNode == node

  def isFromNode(node:SlipnetNode):Boolean = fromNode == node
}