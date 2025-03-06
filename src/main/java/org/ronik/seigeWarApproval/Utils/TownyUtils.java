package org.ronik.seigeWarApproval.Utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class TownyUtils {

    public static boolean isInTown(Player player) {
        if (player == null) return false;

        String location = PlaceholderAPI.setPlaceholders(player, "%townyadvanced_player_location_town_or_wildname%");

        return location != null && !location.equalsIgnoreCase("Wilderness");
    }
}
