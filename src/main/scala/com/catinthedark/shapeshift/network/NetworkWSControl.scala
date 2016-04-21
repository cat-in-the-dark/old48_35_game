package com.catinthedark.shapeshift.network

import java.net.URI
import java.util

import com.badlogic.gdx.math.Vector2
import com.catinthedark.lib.network.IMessageBus.Callback
import com.catinthedark.lib.network.JacksonConverter.CustomConverter
import com.catinthedark.lib.network.messages.GameStartedMessage
import com.catinthedark.lib.network.{JacksonConverter, MessageBus, SocketIOTransport}
import com.fasterxml.jackson.databind.ObjectMapper

class NetworkWSControl(val serverAddress: URI) extends NetworkControl {
  private val objectMapper = new ObjectMapper()
  //objectMapper.registerModule(DefaultScalaModule)
  private val messageConverter = new JacksonConverter(objectMapper)
  private val transport = new SocketIOTransport(messageConverter, serverAddress)
  private val messageBus = new MessageBus(transport)
  
  messageConverter
    .registerConverter[MoveMessage](classOf[MoveMessage].getCanonicalName, new CustomConverter[MoveMessage] {
    override def apply(data: util.Map[String, AnyRef]): MoveMessage = {
      new MoveMessage(
        x = data.get("x").asInstanceOf[Float],
        y = data.get("y").asInstanceOf[Float],
        angle = data.get("angle").asInstanceOf[Float],
        idle = data.get("idle").asInstanceOf[Boolean])
    }
  }).registerConverter[ShootMessage](classOf[ShootMessage].getCanonicalName, new CustomConverter[ShootMessage] {
    override def apply(data: util.Map[String, AnyRef]): ShootMessage = {
      new ShootMessage(
        x = data.get("x").asInstanceOf[Float],
        y = data.get("y").asInstanceOf[Float],
        shotObject = data.get("shotObject").asInstanceOf[String])
    }
  }).registerConverter[JumpMessage](classOf[JumpMessage].getCanonicalName, new CustomConverter[JumpMessage] {
    override def apply(data: util.Map[String, AnyRef]): JumpMessage = {
      new JumpMessage(
        x = data.get("x").asInstanceOf[Float],
        y = data.get("y").asInstanceOf[Float],
        angle = data.get("angle").asInstanceOf[Float],
        scale = data.get("scale").asInstanceOf[Float])
    }
  })
  
  println(messageConverter.registeredConverters())
  
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
