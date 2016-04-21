package com.catinthedark.shapeshift.network

import java.util.concurrent.ConcurrentLinkedQueue

import com.badlogic.gdx.math.Vector2
import com.catinthedark.lib.Pipe
import com.catinthedark.lib.network.messages.Message

case class MoveMessage(x: Float, y: Float, angle: Float, idle: Boolean) extends Message
case class JumpMessage(x: Float, y: Float, angle: Float, scale: Float) extends Message
case class ShootMessage(x: Float, y: Float, shotObject: String) extends Message


trait NetworkControl extends Runnable {
  var isConnected: Option[Unit] = None
  var isMain: Boolean = false

  val onMovePipe = new Pipe[(Vector2, Float, Boolean)]()
  val onShootPipe = new Pipe[(Vector2, String)]()
  val onJumpPipe = new Pipe[(Vector2, Float, Float)]()
  val onEnemyDisconnected = new Pipe[Unit]()

  def move(pos: Vector2, angle: Float, idle: Boolean): Unit = {
    processOut(new MoveMessage(x=pos.x, y=pos.y, angle = angle, idle = idle))
  }

  def shoot(shotFrom: Vector2, objName: String): Unit = {
    processOut(new ShootMessage(x = shotFrom.x, y = shotFrom.y, shotObject = objName))
  }

  def jump(pos: Vector2, angle: Float, scale: Float): Unit = {
    processOut(new JumpMessage(x = pos.x, y = pos.y, angle = angle, scale = scale))
  }

  def processIn() = {
    while(!bufferIn.isEmpty)
      bufferIn.poll()()
  }
  
  def processOut(message: Any)

  protected val bufferIn = new ConcurrentLinkedQueue[() => Unit]()
  
  protected def onMove(msg: (Vector2, Float, Boolean)) = bufferIn.add(() => onMovePipe(msg))
  protected def onShoot(objName: String, shotFrom: Vector2) = bufferIn.add(() => onShootPipe(shotFrom, objName))
  protected def onJump(msg: (Vector2, Float, Float)) = bufferIn.add(() => onJumpPipe(msg))
  protected def onGameStarted(msg: (String, String)) = println(s"Received GameStart package $msg")
}
