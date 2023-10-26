package me.koddydev.fodase.commands

import com.mikael.mkutilslegacy.api.lib.MineCooldown
import com.mikael.mkutilslegacy.api.toTextComponent
import com.mikael.mkutilslegacy.spigot.api.lib.MineCommand
import com.mikael.mkutilslegacy.spigot.api.soundPling
import me.koddydev.fodase.menus.TradeMenu
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class AcceptTradeCommand : MineCommand("accept", "aceitar") {
    companion object {
        lateinit var instance: AcceptTradeCommand
    }

    init {
        instance = this@AcceptTradeCommand
        usage = "/trade accept <jogador>"
    }

    val cooldown = MineCooldown(20 * 3).apply {
        messageOnCooldown = "§cAguarde %time% para aceitar outra troca!"
    }

    override fun playerCommand(player: Player, args: List<String>) {
        if (args.isEmpty()) {
            return sendUsage(player)
        }

        if (cooldown.cooldown(player.name)) {
            val target = args[0]
            val targetPlayer = Bukkit.getPlayer(target) ?: return player.sendMessage("§cEste jogador não está online!")

            if (targetPlayer.name == player.name) {
                return player.sendMessage("§cVocê não pode trocar itens com você mesmo!")
            }

            if (TradeCommand.instance.tradeRequests[targetPlayer.name] == player.name) {
                player.sendMessage("§aTroca aceita com sucesso!")
                player.soundPling()

                targetPlayer.sendMessage("§aTroca aceita com sucesso!")
                targetPlayer.soundPling()

                TradeMenu.instance.tradeRequests[player.name] = targetPlayer.name
                TradeMenu.instance.items[player.name] = mutableListOf()

                TradeMenu.instance.tradeRequests[targetPlayer.name] = player.name
                TradeMenu.instance.items[targetPlayer.name] = mutableListOf()

                TradeMenu.instance.open(player)
                TradeMenu.instance.open(targetPlayer)

                TradeCommand.instance.tradeRequests.remove(targetPlayer.name)
                TradeCommand.instance.tradeRequests.remove(player.name)
            } else {
                player.sendMessage("§cEste jogador não te enviou um pedido de troca!")
            }
        }
    }
}