package me.koddydev.fodase.menus

import com.mikael.mkutilslegacy.api.formatYesNo
import com.mikael.mkutilslegacy.spigot.api.*
import com.mikael.mkutilslegacy.spigot.api.lib.MineItem
import com.mikael.mkutilslegacy.spigot.api.lib.menu.MineMenu
import me.koddydev.fodase.FodaseMain
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class TradeMenu : MineMenu("Pedido de troca", 6) {
    companion object {
        lateinit var instance: TradeMenu
    }

    init {
        instance = this@TradeMenu
        canReceiveItems = true
        isAutoUpdate = true
    }

    val tradeRequests = mutableMapOf<String, String>()
    val items = mutableMapOf<String, MutableList<Pair<ItemStack, Pair<Pair<Int, Int>, Pair<Int, Int>>>>>()
    val pendingAccept = mutableMapOf<String, String>()
    val freePlayerSlots = mutableMapOf<String, List<Pair<Pair<Int, Int>, Pair<Int, Int>>>>()

    override fun update(player: Player) {
        removeAllButtons(player)

        // get the trade request, can be in key or value
        val currentTradeRequest = tradeRequests[player.name]
        FodaseMain.instance.log("currentTradeRequest: $currentTradeRequest of ${player.name}")
        val targetPlayer = Bukkit.getPlayer(currentTradeRequest) ?: return
        val playerItems = items.filter { it.key == player.name }.values.firstOrNull() ?: return
        val itemsToReceive = items[targetPlayer.name] ?: return
        var freeSlots = freePlayerSlots[player.name]

        if (freeSlots.isNullOrEmpty()) {
            freeSlots = mutableListOf(
                (2 to 2) to (6 to 2),
                (3 to 2) to (7 to 2),
                (4 to 2) to (8 to 2),
                (2 to 3) to (6 to 3),
                (3 to 3) to (7 to 3),
                (4 to 3) to (8 to 3),
                (2 to 4) to (6 to 4),
                (3 to 4) to (7 to 4),
                (4 to 4) to (8 to 4),
                (2 to 5) to (6 to 5),
                (3 to 5) to (7 to 5),
                (4 to 5) to (8 to 5)
            )

            freePlayerSlots[player.name] = freeSlots
        }

        val glassPos = listOf(
            2 to 1, 3 to 1, 4 to 1, 5 to 1, 6 to 1, 7 to 1, 8 to 1,
            1 to 2, 9 to 2,
            1 to 3, 9 to 3,
            1 to 4, 9 to 4,
            1 to 5, 9 to 5,
            3 to 6, 4 to 6, 5 to 6, 6 to 6, 7 to 6,
            5 to 2, 5 to 3, 5 to 4, 5 to 5
        )

        glassPos.forEachIndexed { index, pos ->
            button("glassMtuau$index", pos.first, pos.second) {
                icon = MineItem(Material.STAINED_GLASS_PANE)
                    .name("§f")
                    .data(7)

                click = click@{
                    player.soundClick()
                }
            }
        }

        button("currentUserHead", 1, 1) {
            icon = MineItem(Material.SKULL_ITEM)
                .name("§e${player.name}")
                .skull(player.name)
        }

        button("targetUserHead", 9, 1) {
            icon = MineItem(Material.SKULL_ITEM)
                .name("§e${targetPlayer.name}")
                .skull(targetPlayer.name)
        }

        button("MyacceptButton331", 1, 6) {
            icon =
                if (!pendingAccept.any { it.key == player.name }) MineItem(Material.STAINED_GLASS_PANE)
                    .name("§aAceitar troca")
                    .lore("§7Clique para aceitar a troca!")
                    .data(5)
                else MineItem(Material.WOOL)
                    .name("§eAguardando...")
                    .lore("§7Aguarde o jogador ${targetPlayer.name} aceitar a troca!")
                    .data(5)

            click = click@{
                FodaseMain.instance.log(
                    "pendingAccept: ${
                        pendingAccept.any { it.key == player.name }.formatYesNo(true)
                    }"
                )
                if (pendingAccept.any { it.key == targetPlayer.name }) {
                    val playerItems = items.filter { it.key == player.name }.values.firstOrNull() ?: return@click
                    val targetPlayerItems =
                        items.filter { it.key == targetPlayer.name }.values.firstOrNull() ?: return@click

                    if (player.freeSlots < targetPlayerItems.size) {
                        player.sendMessage("§cVocê não tem espaço suficiente no inventário para receber os itens!")
                        targetPlayer.sendMessage("§cO jogador ${player.name} não tem espaço suficiente no inventário para receber os itens!")
                        return@click
                    }

                    if (targetPlayer.freeSlots < playerItems.size) {
                        player.sendMessage("§cO jogador ${targetPlayer.name} não tem espaço suficiente no inventário para receber os itens!")
                        targetPlayer.sendMessage("§cVocê não tem espaço suficiente no inventário para receber os itens!")
                        return@click
                    }

                    // remove item first


                    for (item in playerItems) {
                        FodaseMain.instance.syncTask {
                            targetPlayer.giveItem(item.first)
                        }
                    }

                    for (item in targetPlayerItems) {
                        player.giveItem(item.first)
                    }

                    FodaseMain.instance.syncTask {

                        // remove all players data
                        tradeRequests.remove(player.name)
                        tradeRequests.remove(targetPlayer.name)
                        items.remove(player.name)
                        items.remove(targetPlayer.name)
                        pendingAccept.remove(player.name)
                        pendingAccept.remove(targetPlayer.name)
                        freePlayerSlots.remove(player.name)
                        freePlayerSlots.remove(targetPlayer.name)

                        player.closeInventory()
                        targetPlayer.closeInventory()

                        player.sendMessage("§aTroca realizada com sucesso!")
                        player.soundYes()
                        player.title(
                            "§aTroca realizada com sucesso!",
                            "§7Você trocou itens com o jogador ${targetPlayer.name}!"
                        )

                        targetPlayer.sendMessage("§aTroca realizada com sucesso!")
                        targetPlayer.soundYes()
                        targetPlayer.title(
                            "§aTroca realizada com sucesso!",
                            "§7Você trocou itens com o jogador ${player.name}!"
                        )
                    }
                    return@click
                }

                pendingAccept[player.name] = targetPlayer.name

                player.soundClick()
                targetPlayer.soundPling()

                open(player)
                open(targetPlayer)
            }
        }

        button("MyrejectButton", 2, 6) {
            icon = MineItem(Material.STAINED_GLASS_PANE)
                .name("§cRejeitar troca")
                .lore("§7Clique para rejeitar a troca!")
                .data(14)

            click = click@{
                player.closeInventory()
                targetPlayer.closeInventory()

                player.sendMessage("§cTroca rejeitada com sucesso!")
                player.soundNo()
                player.title(
                    "§cTroca rejeitada com sucesso!",
                    "§7Você rejeitou a troca com o jogador ${targetPlayer.name}!"
                )

                targetPlayer.sendMessage("§cTroca rejeitada com sucesso!")
                targetPlayer.soundNo()
                targetPlayer.title(
                    "§cTroca rejeitada!",
                    "§7O jogador ${player.name} rejeitou a troca!"
                )
            }
        }

        button("rejectButton2", 8, 6) {
            icon = MineItem(Material.STAINED_GLASS_PANE)
                .name("§cRejeitar troca")
                .lore("§7Apenas ${targetPlayer.name} pode utilizar este botão!")
                .data(14)

            click = click@{
                player.soundNo()
            }
        }

        button("acceptButton2", 9, 6) {
            icon = if (!pendingAccept.any { it.key == targetPlayer.name }) MineItem(Material.STAINED_GLASS_PANE)
                .name("§aAceitar troca")
                .lore("§7Apenas ${targetPlayer.name} pode utilizar este botão!")
                .data(5)
            else MineItem(Material.WOOL)
                .name("§eAguardando...")
                .lore("§7O jogador ${targetPlayer.name} está esperando você aceitar a troca!")
                .data(5)


            click = click@{
                player.soundNo()
            }
        }

        for (item in playerItems) {
            val position = item.second

            button("playerItem$position", item.second.first.first, item.second.first.second) {
                icon = item.first

                click = click@{
                    if (items[player.name]?.contains(item) == false) {
                        open(player)
                        open(targetPlayer)
                        player.soundClick()
                        targetPlayer.soundPling()

                        return@click
                    }

                    player.inventory.addItem(item.first)
                    items[player.name]?.remove(item)

                    freePlayerSlots[player.name] = freePlayerSlots[player.name]?.plus(item.second) ?: return@click

                    open(player)
                    open(targetPlayer)
                    player.soundClick()
                    targetPlayer.soundPling()
                }
            }
        }

        for (item in itemsToReceive) {
            val position = item.second

            button(
                "targetPlayerItem${position.first.first}-${position.first.second}",
                item.second.second.first,
                item.second.second.second
            ) {
                icon = item.first

                click = click@{
                    player.soundNo()
                }
            }
        }
    }

    override fun onClose(player: Player) {
        val currentTradeRequest = tradeRequests[player.name] ?: return
        val targetPlayer = Bukkit.getPlayer(currentTradeRequest) ?: return
        val playerItems = items.filter { it.key == player.name }.values.firstOrNull() ?: return
        val itemsToReceive = items.filter { it.key == targetPlayer.name }.values.firstOrNull() ?: return

        // cancel

        playerItems.forEach {
            player.inventory.addItem(it.first)
        }

        itemsToReceive.forEach {
            targetPlayer.inventory.addItem(it.first)
        }

        player.title(
            "§cTroca cancelada!",
            "§7Você cancelou a troca com o jogador ${targetPlayer.name}!"
        )

        targetPlayer.title(
            "§cTroca cancelada!",
            "§7O jogador ${player.name} cancelou a troca!"
        )

        player.soundNo()
        targetPlayer.soundNo()

        tradeRequests.remove(player.name)
        tradeRequests.remove(targetPlayer.name)
        items.remove(player.name)
        items.remove(targetPlayer.name)
        pendingAccept.remove(player.name)
        pendingAccept.remove(targetPlayer.name)
        freePlayerSlots.remove(player.name)
        freePlayerSlots.remove(targetPlayer.name)

        player.closeInventory()
        targetPlayer.closeInventory()
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onClick(e: InventoryClickEvent) {
        val player = e.player
        val item = e.currentItem ?: return

        if (e.clickedInventory == null) return
        if (item.type == Material.AIR) return
        if (tradeRequests[player.name] == null && tradeRequests.filter { it.value == player.name }.keys.firstOrNull() == null) return
        if (e.clickedInventory.holder != player) return
        if (player != e.whoClicked) return

        e.isCancelled = true
        // check if player is trading
        val currentTradeRequest = tradeRequests[player.name]!!
        val targetPlayer = Bukkit.getPlayer(currentTradeRequest) ?: return
        if (targetPlayer == player) return

        val freeSlots = freePlayerSlots[player.name] ?: return
        val playerItems = items.filter { it.key == player.name }.values.firstOrNull() ?: return

        if (freeSlots.isEmpty()) {
            player.sendMessage("§cVocê não tem espaço suficiente no inventário para enviar mais itens!")
            return
        }

        val position = freeSlots.first()

        playerItems.add(item to position)
        freePlayerSlots[player.name] = freeSlots.filter { it != position }

        player.inventory.setItem(e.slot, null)
        open(player)
    }
}