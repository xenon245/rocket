package com.github.devil0414.rocket.command

import com.github.devil0414.rocket.FileManager
import com.github.devil0414.rocket.FileManager.rockets
import com.github.devil0414.rocket.RocketBlock
import com.github.devil0414.rocket.Rockets
import com.github.monun.kommand.KommandBuilder
import com.github.monun.kommand.KommandContext
import com.github.monun.kommand.argument.KommandArgument
import com.github.monun.kommand.argument.string
import com.github.monun.kommand.sendFeedback
import com.sk89q.worldedit.regions.CuboidRegion
import org.bukkit.entity.Player
import com.github.devil0414.rocket.util.selection
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender

object KommandRocket {
    fun register(builder: KommandBuilder) {
        builder.apply {
            then("add") {
                then("name" to string()) {
                    require { this is Player}
                    executes {
                        create(it.sender as Player, it.parseArgument("name"))
                    }
                }
            }
            then("remove") {
                then("rocket" to RocketArgument) {
                    executes {
                        remove(it.sender, it.parseArgument("rocket"))
                    }
                }
            }
            then("launch") {
                then("rocket" to RocketArgument) {
                    require {
                        this is Player
                    }
                    executes {
                        start(it.sender, it.parseArgument("rocket"))
                    }
                }
            }
            then("clear") {
                executes {
                    FileManager.fakeEntityServer.clear()
                }
            }
            then("launchpad") {
                require {
                    this is Player
                }
                executes {
                    launchpad(it.sender as Player)
                }
            }
        }
    }
    private fun create(sender: Player, name: String) {
        sender.selection?.let { region ->
            if(region !is CuboidRegion) {
                sender.sendFeedback("???????????? ?????? ???????????????.")
            } else {
                FileManager.runCatching {
                    createRocket(name, region)
                }.onSuccess {
                    sender.sendFeedback("${it.name} ????????? ??????????????????.")
                }.onFailure {
                    sender.sendFeedback("$name ?????? ????????? ??????????????????.")
                }
            }
        } ?: sender.sendFeedback("????????? ???????????? ?????? worldedit??? wand??? ????????? ??????????????????.")
    }
    private fun remove(sender: CommandSender, rockets: Rockets) {
        rockets.remove()
        sender.sendFeedback("${rockets.name} ????????? ??????????????????.")
    }
    private fun start(sender: CommandSender, rockets: Rockets) {
        val rocket = rockets.startLaunch(locationlaunch)
    }
    private lateinit var locationlaunch : Location
    private fun launchpad(sender: Player) {
        val locationlaunchpad = Location(sender.world, sender.location.x, sender.location.y, sender.location.z)
        locationlaunch = locationlaunchpad
    }
}
object RocketArgument : KommandArgument<Rockets> {
    override fun parse(context: KommandContext, param: String): Rockets? {
        return FileManager.rockets[param]
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return FileManager.rockets.keys.filter { it.startsWith(target, true) }
    }
}