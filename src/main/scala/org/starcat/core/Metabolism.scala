package org.starcat.core

import org.starcat.codelets.CodeletEventListener

trait Metabolism extends CodeletEventListener{
  def start()
  def stop()
  def getComponent():Component
}