package org.ronik.seigeWarApproval.Listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.ronik.seigeWarApproval.SeigeWarApproval;
import org.ronik.seigeWarApproval.Utils.Approval;
import com.palmergames.bukkit.towny.TownyAPI;

import java.util.Map;
import java.util.UUID;

/*
    * This class manages the banner placement event.
    * If a player tries to place a banner without approval, the event is cancelled and siegewar wont be able to access it.
    * If a player has approval, the event is not cancelled and siegewar will be able to access it.
    * If the player is in a town, the plugin will ignore it
 */

public class BannerListener implements Listener {

    private final Map<UUID, Approval> approvedPlayers;
    private final SeigeWarApproval plugin;

    public BannerListener(SeigeWarApproval instance) {
        this.plugin = instance;
        this.approvedPlayers = instance.getApprovedPlayers();
    }

    @EventHandler(priority = EventPriority.LOWEST) // Lowest priority sounds counterintuitive, but it is necessary to cancel the event before siegewar can access it
    public void onBlockPlace(BlockPlaceEvent event) {

        Material placedType = event.getBlockPlaced().getType();

        // if the block placed is not a banner
        if (!placedType.name().contains("BANNER")) {
            return;
        }

        // If the block is not placed in the wilderness, event does not get cancelled
        if (!"Wilderness".equalsIgnoreCase(TownyAPI.getInstance().getTownName(event.getBlock().getLocation()))) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // On no approval
        if (!approvedPlayers.containsKey(playerId)) {
            event.setCancelled(true);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You need approval to start a siege."));
            plugin.getLogger().info("Player " + player.getName() + " tried to place a banner without approval.");
            return;
        }

        Approval approval = approvedPlayers.get(playerId);

        // If approval has expired
        if (System.currentTimeMillis() > approval.getExpirationTime()) {
            approvedPlayers.remove(playerId);
            event.setCancelled(true);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Your approval has expired."));
            plugin.getLogger().info("Player " + player.getName() + " tried to place a banner without approval.");
        }

        // Player is approved, plugin will NOT cancel the event, thus siegewar will access the banner place event
    }
}
