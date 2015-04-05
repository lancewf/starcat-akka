package org.starcat.coderack

import org.starcat.workspace.Workspace
import org.starcat.slipnet.Slipnet
import org.starcat.codelets.ControlCodelet

class CoderackUpdateCodelet extends ControlCodelet {
  
  def execute(workspace:Workspace) {}
    
  def execute(slipent:Slipnet) {}
    
  def execute(coderack:Coderack) {
    coderack.update();
  }
}