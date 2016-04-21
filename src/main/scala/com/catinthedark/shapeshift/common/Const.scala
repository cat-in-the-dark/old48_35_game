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
    debugEnabled
  )

  val debugEnabled = onOff("debug render", false)

  object UI {
    val animationSpeed = 0.2f

    val playerUpWH = vec2Range("player up width height", new Vector2(180, 128))
    val playerUpPhysWH = vec2Range("enemy up phys width height", new Vector2(120, 150))
    
    val enemyUpWH = vec2Range("enemy up width height", new Vector2(180, 128))
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

    val traceDistance = 115
    val traceWH = new Vector2(115, 67)
  }

  object HUD {
    val myProgressPos = vec2Range("my progress bar position", new Vector2(78, 603))
    val enemyProgressPos = vec2Range("enemy progress bar position", new Vector2(814, 603))
  }


  object Projection {
    val width = 1161F
    val height = 652F
    val mapWidth = 3200f
    val mapHeight = 3200f

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

    val distanceHear = 1000f
    def distanceToVolume(distance: Float): Float = {
      // На каждые 200 пикселей громкость падает в 2 раза
      val v = Math.pow(1.5, distance / -200).toFloat
      if (v < 0.01f) {
        0
      } else {
        v
      }
    }

    trait playerBalance {
      val maxRadius: Int
      val viewAngle: Float
      val shotRadius: Int
      val shotDispersionAngle: Float
      val shotColdown: Float
      val tracesLiveTime: Float
    }

    object hunterBalance extends playerBalance {
      override val shotRadius: Int = 1000
      override val shotDispersionAngle: Float = 1
      override val maxRadius: Int = 1000
      override val viewAngle: Float = 2f
      override val shotColdown: Float = 1.3f
      override val tracesLiveTime: Float = 3f
    }

    object wolfBalance extends playerBalance {
      override val maxRadius: Int = 2000
      override val viewAngle: Float = 2000f
      override val shotRadius: Int = 100
      override val shotDispersionAngle: Float = 30
      override val shotColdown: Float = 1.5f
      override val tracesLiveTime: Float = 6f
    }
  }

  val gamerSpeed = frange("speed x", 5F, Some(0), Some(50))
  val gamerJumpSpeed = frange("slow speed x", 10F, Some(0), Some(100))
}
