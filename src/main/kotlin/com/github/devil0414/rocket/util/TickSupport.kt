package com.github.devil0414.rocket.util

object Tick {
    private val INIT_NANO_TIME = System.nanoTime()

    val currentTicks: Long
        get() {
            return (System.nanoTime() - INIT_NANO_TIME) / (50L * 1000L * 1000L)
        }
}