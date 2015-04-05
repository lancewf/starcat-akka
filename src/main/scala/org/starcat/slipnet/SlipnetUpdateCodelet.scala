package org.starcat.slipnet

import org.starcat.workspace.Workspace
import org.starcat.codelets.ControlCodelet
import org.starcat.coderack.Coderack

class SlipnetUpdateCodelet extends ControlCodelet{
   def execute(workspace:Workspace){
      //
      //Do nothing
      //
   }

   def execute(slipent:Slipnet){
     slipent.update()
   }

   def execute(coderack:Coderack){
      //
      //Do nothing
      //
   }
}