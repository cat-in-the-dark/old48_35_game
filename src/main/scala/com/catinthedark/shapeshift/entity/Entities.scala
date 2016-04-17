package com.catinthedark.shapeshift.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.catinthedark.shapeshift.Assets.Animations.PlayerAnimationPack
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.common.Const.Balance.playerBalance
import com.catinthedark.shapeshift.view._

trait Entity {
  var pos: Vector2
  def texture(delta: Float = 0): TextureRegion
}

case class Enemy(var pos: Vector2, var state: State, pack: PlayerAnimationPack, var angle: Float) extends Entity {
  var animationCounter = 0f

  def texture (delta: Float) = {
    state match {
      case IDLE =>
        pack.idle
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
}

case class Player(var pos: Vector2, var state: State, pack: PlayerAnimationPack, balance: playerBalance, var angle: Float) extends Entity {

  var animationCounter = 0f
  var coolDown = false

  def texture (delta: Float) = {
    state match {
      case IDLE => 
        pack.idle
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
}

case class Tree(var pos: Vector2, private val texture: Texture) extends Entity {
  val region = new TextureRegion(texture)
  
  override def texture(delta: Float): TextureRegion = region
}
