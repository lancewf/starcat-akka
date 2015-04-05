package org.starcat.codelets

import org.starcat.coderack.Coderack
import org.starcat.workspace.Workspace
import org.starcat.slipnet.Slipnet

/**
 * Control Codelets are the lowest level codelets. Their interface contract
 * merely requires a specification as to what to do with or in a particular
 * starcat component. Behavior codelets have many more attributes, but also must
 * be define their behavior within the various components. The abstract methods
 * of this class define the execution methods available to all codelets in
 * Starcat.
 */
abstract class ControlCodelet extends Codelet with Cloneable {
   // --------------------------------------------------------------------------
   // Overridden Codelet Members
   // --------------------------------------------------------------------------

   val urgency:Double = 0.0
  
   override def preExecute(coderack:Coderack) { }

   override def preExecute(slipnet:Slipnet) { }


   override def preExecute(workspace:Workspace) { }


   override def postExecute(coderack:Coderack) { }


   override def postExecute(slipnet:Slipnet) { }


   override def postExecute(workspace:Workspace) { }


   override def clone():ControlCodelet = super.clone().asInstanceOf[ControlCodelet]
}