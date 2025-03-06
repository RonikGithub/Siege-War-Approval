package org.ronik.seigeWarApproval.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.ronik.seigeWarApproval.SeigeWarApproval;

import static org.ronik.seigeWarApproval.Utils.Prefix.PREFIX;

import java.util.Arrays;
import java.util.List;

/*
    * This class manages the towny adding rank event.
    * If a player tries to add a restricted rank to a player who has not been on the server for x days, the rank is removed.
 */

public class RankCheckListener implements Listener {

    private final List<String> restrictedRanks;
    private final int requiredPlaytimeHours;

    public RankCheckListener(SeigeWarApproval instance) {
        this.requiredPlaytimeHours = instance.getConfig().getInt("requiredPlaytimeHours", 2);
        this.restrictedRanks = Arrays.asList("private", "sergeant", "lieutenant", "captain", "major", "colonel", "soldier", "general");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] args = message.split(" ");

        if (    args.length < 5 ||
                (!args[0].equalsIgnoreCase("/n") && !args[0].equalsIgnoreCase("/nation")) ||
                !args[1].equalsIgnoreCase("rank") ||
                !args[2].equalsIgnoreCase("add")) {
            return;
        }

        String playerName = args[3];
        String rank = args[4].toLowerCase();

        if (!restrictedRanks.contains(rank)) {
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName); //getOfflinePlayer is deprecated, dm me if you have a better solution
        if (target.hasPlayedBefore()) {
            int playTimeTicks = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
            long playTimeHours = (playTimeTicks / 20L) / 3600L;

            if (playTimeHours < requiredPlaytimeHours) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "townyadmin nation rank remove " + playerName + " " + rank);
                event.getPlayer().sendMessage(PREFIX + ChatColor.RED + "Player " + playerName + " has not played long enough for this rank.");
            }
        } else {
            event.getPlayer().sendMessage(PREFIX + ChatColor.RED + "Player " + playerName + " has never joined the server.");
        }
    }
}
