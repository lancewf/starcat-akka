package org.starcat.workspace

import org.starcat.slipnet.Slipnet
import org.starcat.coderack.Coderack
import org.starcat.codelets.ControlCodelet

class WorkspaceUpdateCodelet extends ControlCodelet {

  def execute(workspace:Workspace) {
    workspace.update();
  }

  def execute(slipent:Slipnet) {
    //
    // Do nothing
    //
  }

  def execute(coderack:Coderack) {
    //
    // Do nothing
    //
  }
}