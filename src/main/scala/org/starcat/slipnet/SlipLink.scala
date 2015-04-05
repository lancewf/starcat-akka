package org.starcat.slipnet

class SlipLink(name:String, intrinsicLength:Int, from:SlipnetNode, to:SlipnetNode, val minShrunkLength:Int, 
    val labelNode:SlipnetNode) 
  extends Link(name, intrinsicLength, from, to) {

  override def getDegreeOfAssociation():Double = {
    return 100 - getCurrentLength();
  }
  
  /**
   * This method first asks this link's label node for its current activation
   * then computes this links shrunk length based on that.
   * 
   * Note: this is not the method called during the spread of activation by a
   * node.
   */
  private def getCurrentLength():Int = {
    val activation = labelNode.getActivation()
    val activationThreshold = labelNode.getActivationThreshold();

    val currentLength = if (activation > activationThreshold) {
      val activationD = activation;
      val activationThresholdD = activationThreshold;
      val maxActivationD = 100.0;
      val intrinsicLengthD = intrinsicLength
      val temp = 1.0 - ((activationD - activationThresholdD) / (maxActivationD - activationThresholdD));

      math.round(intrinsicLengthD * temp).toInt
    } else{
      intrinsicLength
    }
    
    if (currentLength < minShrunkLength){
      minShrunkLength
    } else{
      currentLength
    }
  }
}