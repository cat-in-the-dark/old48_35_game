package com.catinthedark.shapeshift.entity

import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.catinthedark.shapeshift.Assets.Animations.PlayerAnimationPack
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.view._

case class Enemy(var pos: Vector2, var state: State, var frags: Int, pack: PlayerAnimationPack) {
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
case class Player(var pos: Vector2, var state: State, var frags: Int, pack: PlayerAnimationPack) {

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
    new Rectangle(pos.x, pos.y, Const.UI.enemyUpPhysWH().x, Const.UI.enemyUpPhysWH().y)
  }
}
