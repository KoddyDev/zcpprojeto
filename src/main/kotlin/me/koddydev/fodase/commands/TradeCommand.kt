package me.koddydev.fodase.commands

import com.mikael.mkutilslegacy.api.lib.MineCooldown
import com.mikael.mkutilslegacy.api.toTextComponent
import com.mikael.mkutilslegacy.spigot.api.lib.MineCommand
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class TradeCommand : MineCommand("trade", "trocar") {
    companion object {
        lateinit var instance: TradeCommand
    }

    init {
        instance = this@TradeCommand
        usage = "/trade <jogador>"
        registerSubCommand(AcceptTradeCommand())
    }

    val cooldown = MineCooldown(30 * 20).apply {
        messageOnCooldown = "§cAguarde %time% para enviar outro pedido de troca!"
    }

    val tradeRequests = mutableMapOf<String, String>()

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

            val tradeMessage = listOf(
                "§eTrocas",
                "",
                "§7Olá, §f${targetPlayer.name}§7! O jogador §f${player.name}§7 quer trocar itens com você!",
            ).joinToString("\n").toTextComponent()

            tradeMessage.addExtra(
                "\n§fClique §aAQUI §fpara aceitar a troca!".toTextComponent().apply {
                    clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade aceitar ${player.name}")
                    hoverEvent = HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, listOf(
                            "§fClique para aceitar a troca!".toTextComponent()
                        ).toTypedArray()
                    )
                }
            )

            targetPlayer.spigot().sendMessage(tradeMessage)
            tradeRequests[player.name] = targetPlayer.name

            player.sendMessage("§aPedido de troca enviado com sucesso!")
        }
    }
}