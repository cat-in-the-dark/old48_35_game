package com.catinthedark.shapeshift.view

sealed trait State
case object RUNNING extends State
case object SHOOTING extends State
case object KILLED extends State
case object IDLE extends State