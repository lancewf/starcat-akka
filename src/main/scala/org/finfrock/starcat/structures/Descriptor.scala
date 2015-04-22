package org.finfrock.starcat.structures

import akka.actor.ActorRef

class Descriptor(slipnetNodeType:ActorRef, slipnetNodeValue:ActorRef, 
    name1:String, value1:Any) {
  
  protected var descriptorType:ActorRef = slipnetNodeType
  protected var descriptorValue:ActorRef = slipnetNodeValue
  protected var name:String = name1
  protected var value:Any = value1
  protected var object1:Item = null

  def this(name:String, value:Any){
    this(null, null, name, value)
  }
  
  def this(slipnetNodeType:ActorRef, slipnetNodeValue:ActorRef){
    this(slipnetNodeType, slipnetNodeValue, null, null)
  }

  /**
   * true if the two objects reference are the 
   * same (identity) or their respective DescriptionType 
   * and Descriptor objects are equal
   */
  def equals(desc:Descriptor):Boolean = {
    if (this == desc)
    {
      return true;
    }
    if (desc.getDescriptorType() == null)
    {
      return getDescriptorValue().equals(desc.getDescriptorValue());
    }
    if (desc.getDescriptorValue() == null)
    {
      return getDescriptorType().equals(desc.getDescriptorType());
    }
    
    ((getDescriptorType().equals(desc.getDescriptorType()) 
    && getDescriptorValue().equals(desc.getDescriptorValue())))
  }

  
  override def hashCode():Int = {
    return getDescriptorValue().hashCode() 
    + getDescriptorType().hashCode();
  }
  
//  def getConceptualDepth():Int = {
//    return descriptorValue.getConceptualDepth();
//  }
//  
//  def isRelevant():Boolean = {
//    return descriptorType.isActive();
//  }
//  
  def getDescriptorType():ActorRef = {
    return descriptorType;
  }
  
  def setDescriptorType(descriptorType:ActorRef) {
    this.descriptorType = descriptorType;
  }
  
  def getDescriptorValue():ActorRef = descriptorValue
  
  def setDescriptorValue(descriptorValue:ActorRef) {
    this.descriptorValue = descriptorValue;
  }
  
  def getObject():Item = object1
  
  def setObject(object1:Item) {
    this.object1 = object1;
  }
  
  def getName() = name
  def getValue() = value
  
  def isBond(): Boolean = false

  def isDescription() : Boolean = true
  
  def hasDescriptorValue(descriptorValue:ActorRef):Boolean = getDescriptorValue().equals(descriptorValue)
  
  def hasDescriptorType(descriptorType:ActorRef):Boolean = getDescriptorType().equals(descriptorType)
}