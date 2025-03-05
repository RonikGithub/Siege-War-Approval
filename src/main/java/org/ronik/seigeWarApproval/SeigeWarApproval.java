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

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;


public class SeigeWarApproval extends JavaPlugin implements Listener {

    // Map to store approved players along with the expiration time.
    private final HashMap<UUID, Approval> approvedPlayers = new HashMap<>();
    private final String prefix = "[" + ChatColor.GREEN + "SiegeWarApproval" + ChatColor.RESET + "] ";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Bukkit.getLogger().warning("PlaceholderAPI is not installed! Some features may not work.");
        }
        getServer().getPluginManager().registerEvents(new BannerListener(approvedPlayers), this);
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("dma")).setExecutor(new DMACommand());
        getLogger().info("Ronik's Siege War Approval Plugin has been enabled. Use /dma <approve|disapprove> <player> to approve or disapprove a player.");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic if needed
        getLogger().info("Ronik's Siege War Approval Plugin has been disabled.");
    }

    // Command executor for /dma approve <player>
    public class DMACommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            // Check if the sender has the required permission.
            if (!sender.hasPermission("dma.approve")) {
                sender.sendMessage(prefix + "You do not have permission to use this command.");
                return true;
            }

            // Command usage: /dma <approve|disapprove> <player>
            if (args.length < 2 || (!args[0].equalsIgnoreCase("approve") && !args[0].equalsIgnoreCase("disapprove"))) {
                sender.sendMessage(prefix + "Usage: /dma <approve|disapprove> <player>");
                return true;
            }

            String targetPlayerName = args[1];

            // Get the target player (must be online)
            Player targetPlayer = getServer().getPlayerExact(targetPlayerName);
            if (targetPlayer == null) {
                sender.sendMessage(prefix + targetPlayerName + " not found.");
                return true;
            }

            // on /dma approve <player>
            if (args[0].equalsIgnoreCase("approve")) {

                // Get the approval duration from the config (in minutes)
                int minutes = getConfig().getInt("approvalMinutes", 60);
                long expirationTime = System.currentTimeMillis() + ((long) minutes * 60 * 1000);
                Approval approval = new Approval(expirationTime);
                approvedPlayers.put(targetPlayer.getUniqueId(), approval);

                sender.sendMessage(prefix + "Player " +
                        targetPlayerName + " approved for " + minutes + " minutes.");


                targetPlayer.sendMessage(prefix +
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
                                targetPlayer.sendMessage(prefix + "Your approval has expired.");
                            }
                        }
                    }
                }.runTaskLater(SeigeWarApproval.this, minutes * 60 * 20L); // minutes * 60 seconds * 20 ticks/sec

            }

            // on /dma disapprove <player>
            else if (args[0].equalsIgnoreCase("disapprove")) {
                if (approvedPlayers.containsKey(targetPlayer.getUniqueId())) {
                    approvedPlayers.remove(targetPlayer.getUniqueId());
                    sender.sendMessage(prefix + "Player " +
                            targetPlayerName + " disapproved.");
                    targetPlayer.sendMessage(prefix + "Your approval has been revoked.");
                } else {
                    sender.sendMessage(prefix + "Player " + targetPlayerName + " is not approved.");
                }
            }

            return true;
        }
    }
}
