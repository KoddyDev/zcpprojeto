package me.koddydev.fodase

import com.mikael.mkutilslegacy.api.mkplugin.MKPlugin
import com.mikael.mkutilslegacy.api.mkplugin.MKPluginSystem
import me.koddydev.fodase.commands.AcceptTradeCommand
import me.koddydev.fodase.commands.TradeCommand
import me.koddydev.fodase.menus.TradeMenu
import net.eduard.api.lib.modules.BukkitTimeHandler
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class FodaseMain: JavaPlugin(), MKPlugin, BukkitTimeHandler {

    companion object {
        lateinit var instance: FodaseMain
    }

    override fun onEnable() {
        instance = this@FodaseMain
        val start = System.currentTimeMillis()

        log("§eInicializando sistemas...")

        TradeCommand().registerCommand(this)
        TradeMenu().registerMenu(this)

        log("§eSistemas inicializados com sucesso! §c(${System.currentTimeMillis() - start}ms)")

        MKPluginSystem.registerMKPlugin(this)
    }

    override fun getPlugin(): Any {
        return this
    }

    override fun getPluginFolder(): File {
        return dataFolder
    }

    override fun getSystemName(): String {
        return "Fodase"
    }

    override fun log(vararg msg: String) {
        val sender = server.consoleSender

        msg.forEach { message ->
            sender.sendMessage("§b[Fodase] §f$message")
        }
    }

    override fun getPluginConnected(): Plugin {
        return this
    }

}