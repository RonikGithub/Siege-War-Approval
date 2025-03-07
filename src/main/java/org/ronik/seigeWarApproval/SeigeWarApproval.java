package org.ronik.seigeWarApproval;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import org.ronik.seigeWarApproval.Listeners.BannerListener;
import org.ronik.seigeWarApproval.Listeners.RankCheckListener;
import org.ronik.seigeWarApproval.Utils.Approval;

import static org.ronik.seigeWarApproval.Utils.Prefix.PREFIX;
import static org.ronik.seigeWarApproval.Utils.Prefix.RONIK_ART;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;


public class SeigeWarApproval extends JavaPlugin implements Listener {

    // Map to store approved players along with the expiration time.
    private final HashMap<UUID, Approval> approvedPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new BannerListener(this), this);
        getServer().getPluginManager().registerEvents(new RankCheckListener(this), this);
        Objects.requireNonNull(getCommand("dma")).setExecutor(new DMACommand());
        getLogger().info("Ronik's Siege War Approval Plugin has been enabled. Use /dma <approve|disapprove> <player> to approve or disapprove a player.");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Bukkit.getLogger().warning("PlaceholderAPI is not installed! Some features may not work.");
        }
        getLogger().info(RONIK_ART);
    }

    @Override
    public void onDisable() {
        getLogger().info("Ronik's Siege War Approval Plugin has been disabled.");
    }

    public HashMap<UUID, Approval> getApprovedPlayers() {
        return approvedPlayers;
    }

    // Command executor for /dma approve <player>.
    public class DMACommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

            // Check if the sender has the required permission.
            if (!sender.hasPermission("dma.approve")) {
                sender.sendMessage(PREFIX + "You do not have permission to use this command.");
                return true;
            }

            // Command usage: /dma <approve|disapprove> <player>
            if (args.length < 2 || (!args[0].equalsIgnoreCase("approve") && !args[0].equalsIgnoreCase("disapprove"))) {
                sender.sendMessage(PREFIX + "Usage: /dma <approve|disapprove> <player>");
                return true;
            }

            String targetPlayerName = args[1];

            // Get the target player (must be online)
            Player targetPlayer = getServer().getPlayerExact(targetPlayerName);
            if (targetPlayer == null) {
                sender.sendMessage(PREFIX + targetPlayerName + " not found.");
                return true;
            }

            // on /dma approve <player>
            if (args[0].equalsIgnoreCase("approve")) {

                // Get the approval duration from the config (in minutes)
                int minutes = getConfig().getInt("approvalMinutes", 60);
                long expirationTime = System.currentTimeMillis() + ((long) minutes * 60 * 1000);
                Approval approval = new Approval(expirationTime);
                approvedPlayers.put(targetPlayer.getUniqueId(), approval);

                sender.sendMessage(PREFIX + "Player " +
                        targetPlayerName + " approved for " + minutes + " minutes.");


                targetPlayer.sendMessage(PREFIX +
                        "You have been approved to start a siege for " + ChatColor.GREEN + minutes +
                        ChatColor.RESET + " minutes.");


                // Schedule a task to remove the approval after the specified duration.
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (approvedPlayers.containsKey(targetPlayer.getUniqueId())) {
                            Approval currentApproval = approvedPlayers.get(targetPlayer.getUniqueId());
                            if (currentApproval.getExpirationTime() <= System.currentTimeMillis()) {
                                approvedPlayers.remove(targetPlayer.getUniqueId());
                                targetPlayer.sendMessage(PREFIX + "Your approval has expired.");
                            }
                        }
                    }
                }.runTaskLater(SeigeWarApproval.this, minutes * 60 * 20L); // minutes * 60 seconds * 20 ticks/sec

            }

            // on /dma disapprove <player>
            else if (args[0].equalsIgnoreCase("disapprove")) {
                if (approvedPlayers.containsKey(targetPlayer.getUniqueId())) {
                    approvedPlayers.remove(targetPlayer.getUniqueId());
                    sender.sendMessage(PREFIX + "Player " +
                            targetPlayerName + " disapproved.");
                    targetPlayer.sendMessage(PREFIX + "Your approval has been revoked.");
                } else {
                    sender.sendMessage(PREFIX + "Player " + targetPlayerName + " is not approved.");
                }
            }

            return true;
        }
    }
}
