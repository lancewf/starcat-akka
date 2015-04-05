package org.starcat.structures

import org.starcat.slipnet.SlipnetNode

class Descriptor(slipnetNodeType:SlipnetNode, slipnetNodeValue:SlipnetNode, 
    name1:String, value1:Any) {
  
  protected var descriptorType:SlipnetNode = slipnetNodeType
  protected var descriptorValue:SlipnetNode = slipnetNodeValue
  protected var name:String = name1
  protected var value:Any = value1
  protected var object1:Item = null

  def this(name:String, value:Any){
    this(null, null, name, value)
  }
  
  def this(slipnetNodeType:SlipnetNode, slipnetNodeValue:SlipnetNode){
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
  
  def getConceptualDepth():Int = {
    return descriptorValue.getConceptualDepth();
  }
  
  def isRelevant():Boolean = {
    return descriptorType.isActive();
  }
  
  def getDescriptorType():SlipnetNode = {
    return descriptorType;
  }
  
  def setDescriptorType(descriptorType:SlipnetNode) {
    this.descriptorType = descriptorType;
  }
  
  def getDescriptorValue():SlipnetNode = descriptorValue
  
  def setDescriptorValue(descriptorValue:SlipnetNode) {
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
  
  def hasDescriptorValue(descriptorValue:SlipnetNode):Boolean = getDescriptorValue().equals(descriptorValue)
  
  def hasDescriptorType(descriptorType:SlipnetNode):Boolean = getDescriptorType().equals(descriptorType)

  def describes(entity:Entity):Boolean = entity.getDescriptors().exists(_ == this)
}