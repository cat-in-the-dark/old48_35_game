package com.catinthedark.shapeshift.network

import java.util.concurrent.ConcurrentLinkedQueue

import com.badlogic.gdx.math.Vector2
import com.catinthedark.lib.Pipe
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.entity.Entity
import org.zeromq.ZMQ
import org.zeromq.ZMQ.{PollItem, Poller, Socket}

trait NetworkControl extends Runnable {
  var isConnected: Option[Unit] = None

  val MOVE_PREFIX = "MOVE"
  val SHOOT_PREFIX = "SHOOT"
  val ILOOSE_PREFIX = "ILOOSE"
  val IWON_PREFIX = "IWON"
  val HELLO_PREFIX = "HELLO"
  val ALIVE_PREFIX = "ALIVE"

  val buffer = new ConcurrentLinkedQueue[String]()
  val bufferIn = new ConcurrentLinkedQueue[() => Unit]()

  val onMovePipe = new Pipe[(Vector2, Float, Boolean)]()
  val onShootPipe = new Pipe[(Vector2, String)]()
  val onILoosePipe = new Pipe[Unit]()
  val onIWonPipe = new Pipe[Unit]()
  val onAlivePipe = new Pipe[Unit]()

  def onMove(msg: (Vector2, Float, Boolean)) = bufferIn.add(() => onMovePipe(msg))
  def onShoot(objName: String, shotFrom: Vector2) = bufferIn.add(() => onShootPipe(shotFrom, objName))
  def onILoose() = bufferIn.add(() => onILoosePipe())
  def onIWon() = bufferIn.add(() => onIWonPipe())
  def onAlive() = bufferIn.add(() => onAlivePipe())
  
  def onHello(pushSocket: Socket) = println("Received hello package")

  def move(pos: Vector2, angle: Float, idle: Boolean): Unit = {
    buffer.add(s"$MOVE_PREFIX:${pos.x};${pos.y};$angle;$idle")
  }

  def shoot(shotFrom: Vector2, objName: String): Unit = {
    buffer.add(s"$SHOOT_PREFIX:$objName;${shotFrom.x};${shotFrom.y}")
  }

  def iLoose(): Unit = {
    buffer.add(s"$ILOOSE_PREFIX:")
  }

  def iWon(): Unit = {
    buffer.add(s"$IWON_PREFIX:")
  }

  def iAlive(): Unit = {
    buffer.add(s"$ALIVE_PREFIX:")
  }

  def processIn() = {
    while(!bufferIn.isEmpty)
      bufferIn.poll()()
  }

  def isServer: Boolean
  
  def work(pushSocket: Socket, pullSocket: Socket): Unit = {
    val pollItems = Array(new PollItem(pullSocket, Poller.POLLIN), new PollItem(pushSocket, Poller.POLLOUT))
    var shouldStop: Boolean = false
    var detectedGameEnd: Boolean = false

    while (!shouldStop && !Thread.currentThread().isInterrupted) {
      try {
        ZMQ.poll(pollItems, Const.pollTimeout)
        if (pollItems(0).isReadable) {
          val rawData = pullSocket.recvStr()
          val data = rawData.split(":")

          data(0) match {
            case MOVE_PREFIX =>
              val attrs = data(1).split(";")
              val pos = new Vector2(attrs(0).toFloat, attrs(1).toFloat)
              val angle = attrs(2).toFloat
              val idle = attrs(3).toBoolean
              onMove(pos, angle, idle)
            case SHOOT_PREFIX =>
              val attrs = data(1).split(";")
              val objName = attrs(0)
              val posX = attrs(1).toFloat
              val posY = attrs(2).toFloat
              onShoot(objName, new Vector2(posX, posY))
            case ILOOSE_PREFIX =>
              detectedGameEnd = true
              onILoose()
            case IWON_PREFIX =>
              detectedGameEnd = true
              onIWon()
            case HELLO_PREFIX =>
              onHello(pushSocket)
              isConnected = Some()
            case ALIVE_PREFIX =>
              println("enemy alive")
              onAlive()
            case _ => println(s"UPS, wrong prefix $rawData")
          }
        }

        if (!buffer.isEmpty && pollItems(1).isWritable) {
          val message = buffer.poll()
          pushSocket.send(message)
          if (message.startsWith(IWON_PREFIX) || message.startsWith(ILOOSE_PREFIX)) {
            detectedGameEnd = true
            shouldStop = true
          }
        }
      } catch {
        case e: InterruptedException =>
          println("Interrupted network thread")
          shouldStop = true
      }
    }

    if (!detectedGameEnd) {
      pushSocket.send(s"$IWON_PREFIX:")
    }

    buffer.clear()
    bufferIn.clear()
    pullSocket.close()
    pushSocket.close()
    isConnected = None
    println("Connection closed")
  }
}
