package org.starcat.codelets

import java.util.EventObject

case class CodeletEvent( source1:Any, val codelet:Codelet) extends EventObject(source1)