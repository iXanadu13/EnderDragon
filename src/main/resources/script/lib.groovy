//package script

import org.bukkit.Bukkit
import org.bukkit.entity.Player

//def tell = player.&sendMessage
def tell(Object raw){
    player.sendMessage(raw.toString())
}