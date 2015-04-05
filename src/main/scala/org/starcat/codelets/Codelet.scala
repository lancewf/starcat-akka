package org.starcat.codelets

import org.joda.time.DateTime
import org.starcat.coderack.Coderack
import org.starcat.slipnet.Slipnet
import org.starcat.workspace.Workspace

trait Codelet {
  
  // -----------------------------------------------------------------------------
  // Abstract Members
  // -----------------------------------------------------------------------------

  val urgency:Double
  
  def execute(coderack:Coderack)

  def execute(slipnet:Slipnet)

  def execute(workspace:Workspace)

  
  def preExecute(coderack:Coderack)

  def preExecute(slipnet:Slipnet)

  def preExecute(workspace:Workspace)

  
  def postExecute(coderack:Coderack)

  def postExecute(slipnet:Slipnet)

  def postExecute(workspace:Workspace)

  // -----------------------------------------------------------------------------
  // Public Members
  // -----------------------------------------------------------------------------

  /**
   * Default implementation of long-living codelets Instantiating applications
   * may wish to overload and obtain finite lifetimes for codelets
   * Coderack.update is responsible for removal
   */
  def getTimeToDie():DateTime = {
    return DateTime.now().plusYears(1);
  }
}