package com.catinthedark.shapeshift

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.badlogic.gdx.{Gdx, utils}
import com.catinthedark.shapeshift.common.Const

object Assets {
  object Textures {
    val logo = new Texture(Gdx.files.internal("textures/logo.png"))

//    val t0 = new Texture(Gdx.files.internal("textures/start_screen.png"))
//    val t1 = new Texture(Gdx.files.internal("textures/tut1eng.png"))
//    val t2 = new Texture(Gdx.files.internal("textures/tut2eng.png"))
//    val t3 = new Texture(Gdx.files.internal("textures/tut3eng.png"))
//    val t4 = new Texture(Gdx.files.internal("textures/tut4eng.png"))
//    val t4 = new Texture(Gdx.files.internal("textures/tut5eng.png"))
    trait ThemePack {
      val winScreen: Texture
      val pairing: Texture
      val loseScreen: Texture
    }
    
    object GoodThemePack extends ThemePack {
      override val winScreen: Texture = new Texture(Gdx.files.internal("textures/logo.png"))
      override val pairing: Texture = new Texture(Gdx.files.internal("textures/logo.png"))
      override val loseScreen: Texture = new Texture(Gdx.files.internal("textures/logo.png"))
    }

    object WolfThemePack extends ThemePack {
      override val winScreen: Texture = new Texture(Gdx.files.internal("textures/logo.png"))
      override val pairing: Texture = new Texture(Gdx.files.internal("textures/logo.png"))
      override val loseScreen: Texture = new Texture(Gdx.files.internal("textures/logo.png"))
    }

  }

  object Fonts {
    val mainGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/main.ttf"))
    val moneyFontParam = new FreeTypeFontParameter()
    moneyFontParam.size = 44
    val hudFont = mainGenerator.generateFont(moneyFontParam)
    hudFont.setColor(92f / 255, 85f / 255, 103f / 255, 1)

    val ctrlFont = mainGenerator.generateFont(moneyFontParam)
    ctrlFont.setColor(224f / 255, 248f / 255, 18f / 255, 1)
  }

  object Animations {
    private def loopingAnimation(frames: Array[Array[TextureRegion]], frameIndexes: (Int, Int)*): Animation = {
      val array = new utils.Array[TextureRegion]
      frameIndexes.foreach(i => array.add(frames(i._1)(i._2)))
      new Animation(Const.UI.animationSpeed, array, Animation.PlayMode.LOOP)
    }

    private def normalAnimation(speed: Float, frames: Array[Array[TextureRegion]], frameIndexes: (Int, Int)*): Animation = {
      val array = new utils.Array[TextureRegion]
      frameIndexes.foreach(i => array.add(frames(i._1)(i._2)))
      new Animation(speed, array, Animation.PlayMode.NORMAL)
    }
    
    trait PlayerAnimationPack
  }

  object Audios {
  }
}
