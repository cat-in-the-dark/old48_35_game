package com.catinthedark.shapeshift.common

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.catinthedark.lib.constants.ConstDelegate

import scala.util.Random

/**
  * Created by over on 11.12.15.
  */
object Const extends ConstDelegate {
  override def delegate = Seq(
    debugEnabled,
    UI.playerMinX,
    HUD.myFragsPos,
    HUD.enemyFragsPos,
    HUD.ctrl1Pos,
    HUD.ctrl2Pos
  )

  val debugEnabled = onOff("debug render", false)

  object UI {
    val animationSpeed = 0.2f

    val enemyYRange = vec2Range("enemy parallax move", new Vector2(303, 415))
    val enemyParallaxSpeed = frange("enemy parallax speed", 793, Some(300), Some(1000f))

    val enemyBackYRange = vec2Range("enemy back parallax move", new Vector2(100, 175))
    val enemyBackParallaxSpeed = frange("enemy back parallax speed", 500, Some(300), Some(1000f))

    val myHedgeYRange = vec2Range("myHedge parallax move", new Vector2(-31, 87))
    val myHedgeParallaxSpeed = frange("hedge parallax move", 1100f, Some(800f), Some(1500f))

    val groundYRange = vec2Range("myHedge parallax move", new Vector2(-47, 0))
    val groundParallaxSpeed = frange("hedge parallax move", 800, Some(600), Some(1500f))

    val roadYRange = vec2Range("road parallax move", new Vector2(257, 257))
    val roadParallaxSpeed = frange("road parallax move", 800, Some(500), Some(1000))

    val enemyHedgeYRange = vec2Range("enemy hedge parallax move", new Vector2(247, 290))
    val enemyHedgeParallaxSpeed = frange("enemy hedge parallax move", 720, Some(500), Some(1000))
    
    val playerY = frange("player y", 10, Some(0), Some(500))
    val playerMinX = frange("player min x", 41, Some(0), Some(500))
    val playerUpWH = vec2Range("player up width height", new Vector2(180, 128))
    val playerDownWH = vec2Range("player down width height", new Vector2(320, 300))
    val playerUpPhysWH = vec2Range("enemy up phys width height", new Vector2(120, 150))
    
    val enemyY = frange("enemy y", 415, Some(100), Some(652))
    val enemyUpWH = vec2Range("enemy up width height", new Vector2(180, 128))
    val enemyDownWH = vec2Range("enemy down width height", new Vector2(80, 96))

    val enemyUpPhysWH = vec2Range("enemy up phys width height", new Vector2(120, 150))
    val treePhysRadius = 180f / 2
    val playerPhysRadius = 40f

    val rayLength = 1000

    val darknessRed = 0.04f
    var darknessGreen = 0.04f
    var darknessBlue = 0.157f

    val darknessColor = new Color(darknessRed, darknessGreen, darknessBlue, 1f)
    val semiDarknessColor = new Color(darknessRed, darknessGreen, darknessBlue, 0.5f)
    val halfShadowAngle = 5f * Math.PI / 180

    val maxJumpingScale = 2.0f
  }

  object HUD {
    val myProgressPos = vec2Range("my progress bar position", new Vector2(78, 603))
    val enemyProgressPos = vec2Range("enemy progress bar position", new Vector2(814, 603))
    val progressWh = vec2Range("player down width height", new Vector2(252, 25))
    val myFragsPos = vec2Range("my frag pos", new Vector2(453, 630))
    val enemyFragsPos = vec2Range("enemy frag pos", new Vector2(670, 630))

    val waterBarPos = vec2Range("water bar pos", new Vector2(16, 55))
    val waterBarWh = vec2Range("water bar width height", new Vector2(24, 248))
    val ctrl1Pos = vec2Range("ctrl1 pos", new Vector2(103, 56))
    val ctrl2Pos = vec2Range("ctrl1 pos", new Vector2(897, 56))
  }


  object Projection {
    val width = 1161F
    val height = 652F
    val mapWidth = 3200f
    val mapHeight = 3200f
    
    val enemyOffsetX = 20F
    val enemyViewPort = width - enemyOffsetX * 2
    def projection(originX: Float): Float = originX * enemyViewPort / width
    
    def calcX(screenX: Int): Int = (screenX.toFloat * Const.Projection.width / Gdx.graphics.getWidth).toInt
    def calcY(screenY: Int): Int = (screenY.toFloat * Const.Projection.height / Gdx.graphics.getHeight).toInt
  }

  object Balance {
    val spawnPoints  = Array(
      (new Vector2(100, 100), new Vector2(570,2700)),
      (new Vector2(1670, 2500), new Vector2(2500,2700))
    )
    def randomSpawn = {
      val ab = spawnPoints(new Random().nextInt(spawnPoints.length))
      (ab._1.cpy(), ab._2.cpy())
    }

    val jumpTime = 1f
    val jumpCoolDown = 5f

    trait playerBalance {
      val maxRadius: Int
      val viewAngle: Float
      val shotRadius: Int
      val shotDispersionAngle: Float
      val shotColdown: Float
    }

    object hunterBalance extends playerBalance {
      override val shotRadius: Int = 1000
      override val shotDispersionAngle: Float = 1
      override val maxRadius: Int = 1000
      override val viewAngle: Float = 2f
      override val shotColdown: Float = 1.3f
    }

    object wolfBalance extends playerBalance {
      override val maxRadius: Int = 2000
      override val viewAngle: Float = 2000f
      override val shotRadius: Int = 200
      override val shotDispersionAngle: Float = 30
      override val shotColdown: Float = 1.5f
    }
  }

  val serverPullPort = 9000
  val serverPushPort = 9001
  val pollTimeout = 10
  
  val gamerSpeed = frange("speed x", 5F, Some(0), Some(50))
  val gamerJumpSpeed = frange("slow speed x", 10F, Some(0), Some(100))
}
