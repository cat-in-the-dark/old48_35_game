package com.catinthedark.shapeshift.entity

import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.catinthedark.shapeshift.Assets.Animations.PlayerAnimationPack
import com.catinthedark.shapeshift.common.Const
import com.catinthedark.shapeshift.view._

case class Enemy(var pos: Vector2, var state: State, var frags: Int, pack: PlayerAnimationPack) {
  var animationCounter = 0f
  
  def texture (delta: Float) = {
    state match {
      case _ =>
        pack.running
    }
//    state match {
//      case UP => pack.up
//      case SHOOTING =>
//        animationCounter += delta
//        pack.shooting.getKeyFrame(animationCounter)
//      case DOWN => pack.up
//      case _ =>
//        println(s"Unknown enemy state $state")
//        pack.up
//    }
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
//    state match {
//      case UP => pack.up
//      case SHOOTING =>
//        animationCounter += delta
//        pack.shooting.getKeyFrame(animationCounter)
//      case DOWN => pack.down
//      case RUNNING =>
//        animationCounter += delta
//        pack.running.getKeyFrame(animationCounter)
//      case CRAWLING =>
//        animationCounter += delta
//        pack.crawling.getKeyFrame(animationCounter)
//      case KILLED => pack.killed
//    }
  }

  def rect: Rectangle = {
    new Rectangle(pos.x, pos.y, Const.UI.playerUpWH().x, Const.UI.playerUpWH().y)
  }

  def physRect: Rectangle = {
    new Rectangle(pos.x, pos.y, Const.UI.enemyUpPhysWH().x, Const.UI.enemyUpPhysWH().y)
  }
}
