package com.catinthedark.shapeshift.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.catinthedark.shapeshift.Assets.Animations.PlayerAnimationPack
import com.catinthedark.shapeshift.Assets.Audios.PlayerAudioPack
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.common.Const.Balance.playerBalance
import com.catinthedark.shapeshift.common.Const.UI
import com.catinthedark.shapeshift.view._

sealed trait Entity {
  var pos: Vector2
  var radius: Float
  var angle: Float
  def texture(delta: Float = 0): TextureRegion
  def name: String
}

case class Enemy(var pos: Vector2, var state: State, pack: PlayerAnimationPack, audio: PlayerAudioPack, balance: playerBalance, var angle: Float, var scale: Float) extends Entity {
  var animationCounter = 0f

  def texture (delta: Float) = {
    state match {
      case IDLE =>
        pack.idle
      case SHOOTING =>
        animationCounter += delta
        pack.shooting.getKeyFrame(animationCounter)
      case _ =>
        animationCounter += delta
        pack.running.getKeyFrame(animationCounter)
    }
  }

  def rect: Rectangle = {
    new Rectangle(pos.x, pos.y, Const.UI.enemyUpWH().x, Const.UI.enemyUpWH().y)
  }

  def physRect: Rectangle = {
    new Rectangle(pos.x, pos.y, Const.UI.enemyUpPhysWH().x, Const.UI.enemyUpPhysWH().y)
  }

  override var radius: Float = UI.playerPhysRadius

  override def name: String = "Enemy"
}

case class Player(var pos: Vector2, var state: State, pack: PlayerAnimationPack, audio: PlayerAudioPack, balance: playerBalance, var angle: Float, var scale: Float, var canJump: Boolean, var canShot: Boolean = true) extends Entity {

  var animationCounter = 0f

  def texture (delta: Float) = {
    state match {
      case IDLE => 
        pack.idle
      case SHOOTING =>
        animationCounter += delta
        pack.shooting.getKeyFrame(animationCounter)
      case _ =>
        animationCounter += delta
        pack.running.getKeyFrame(animationCounter)
    }
  }

  def rect: Rectangle = {
    new Rectangle(pos.x, pos.y, Const.UI.playerUpWH().x, Const.UI.playerUpWH().y)
  }

  def physRect: Rectangle = {
    new Rectangle(pos.x, pos.y, Const.UI.playerUpPhysWH().x, Const.UI.playerUpPhysWH().y)
  }
  
  override def name: String = "Player"

  override var radius: Float = UI.playerPhysRadius
}

case class Tree(var pos: Vector2, private val texture: Texture) extends Entity {
  val region = new TextureRegion(texture)
  
  override def texture(delta: Float): TextureRegion = region

  override var radius: Float = UI.treePhysRadius

  override def name: String = "Tree"

  override var angle: Float = 0f
}

case class Trace(var pos: Vector2, var angle: Float) {
  def rect: Rectangle = {
    new Rectangle(pos.x, pos.y, UI.traceWH.x, UI.traceWH.y)
  }
}
