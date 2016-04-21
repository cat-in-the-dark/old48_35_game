package com.catinthedark.shapeshift.network

import java.net.URI

import com.badlogic.gdx.math.Vector2
import com.catinthedark.lib.network.IMessageBus.Callback
import com.catinthedark.lib.network.messages.GameStartedMessage
import com.catinthedark.lib.network.{JacksonConverterScala, MessageBus, SocketIOTransport}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class NetworkWSControl(val serverAddress: URI) extends NetworkControl {
  private val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)
  private val messageConverter = new JacksonConverterScala(objectMapper)
  private val transport = new SocketIOTransport(messageConverter, serverAddress)
  private val messageBus = new MessageBus(transport)
  
  messageConverter
    .registerConverter[MoveMessage](classOf[MoveMessage], data => {
    new MoveMessage(
      x = data("x").asInstanceOf[Double].toFloat,
      y = data("y").asInstanceOf[Double].toFloat,
      angle = data("angle").asInstanceOf[Double].toFloat,
      idle = data("idle").asInstanceOf[Boolean])
  }).registerConverter[ShootMessage](classOf[ShootMessage], data => {
    new ShootMessage(
      x = data("x").asInstanceOf[Double].toFloat,
      y = data("y").asInstanceOf[Double].toFloat,
      shotObject = data("shotObject").asInstanceOf[String])
  }).registerConverter[JumpMessage](classOf[JumpMessage], data => {
    new JumpMessage(
      x = data("x").asInstanceOf[Double].toFloat,
      y = data("y").asInstanceOf[Double].toFloat,
      angle = data("angle").asInstanceOf[Double].toFloat,
      scale = data("scale").asInstanceOf[Double].toFloat)
  })
  
  println(s"Converters ${messageConverter.registeredConverters}")
  
  messageBus.subscribe(classOf[MoveMessage], new Callback[MoveMessage] {
    override def apply(message: MoveMessage, sender: String): Unit = {
      onMove(new Vector2(message.x, message.y), message.angle, message.idle)
    }
  })

  messageBus.subscribe(classOf[ShootMessage], new Callback[ShootMessage] {
    override def apply(message: ShootMessage, sender: String): Unit = {
      onShoot(message.shotObject, new Vector2(message.x, message.y))
    }
  })

  messageBus.subscribe(classOf[JumpMessage], new Callback[JumpMessage] {
    override def apply(message: JumpMessage, sender: String): Unit = {
      onJump(new Vector2(message.x, message.y), message.angle, message.scale)
    }
  })
  
  messageBus.subscribe(classOf[GameStartedMessage], new Callback[GameStartedMessage] {
    override def apply(message: GameStartedMessage, sender: String): Unit = {
      isConnected = Some()
      onGameStarted(message.getClientID, message.getRole)
    }
  })
  
  override def run(): Unit = {
    transport.connect()
  }

  override def processOut(message: Any): Unit = {
    messageBus.send(message)
  }
}
