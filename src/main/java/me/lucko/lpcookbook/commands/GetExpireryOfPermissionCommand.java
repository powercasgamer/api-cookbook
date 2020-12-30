package me.lucko.lpcookbook.commands;

import me.lucko.lpcookbook.CookbookPlugin;
import me.lucko.lpcookbook.util.TimeUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

public class GetExpireryOfPermissionCommand implements CommandExecutor {
    private final CookbookPlugin plugin;
    private final LuckPerms luckPerms;

    public GetExpireryOfPermissionCommand(CookbookPlugin plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Please specify a player & a permission!");
            return true;
        }

        String playerName = args[0];
        String permission = args[1];

        // Get an OfflinePlayer object for the player
        Player player = this.plugin.getServer().getPlayer(playerName);

        // Player not known?
        if (player == null || !player.isOnline()) {
            sender.sendMessage(ChatColor.RED + playerName + " is not online!");
            return true;
        }

        Node node = PermissionNode.builder(permission).build();
        User user = this.luckPerms.getUserManager().getUser(player.getUniqueId());
        //this.luckPerms.getUserManager().loadUser(player.getUniqueId(), (User user) -> {

            long duration = user.getNodes(NodeType.PERMISSION)
                    .stream()
                    .filter(n -> n.getKey().equals(node.getKey()))
                    .filter(PermissionNode::hasExpiry)
                    .map(PermissionNode::getExpiryDuration)
                    .mapToLong(Duration::toMillis).findFirst().orElse(0);

            sender.sendMessage(TimeUtil.millisToRoundedTime(duration));
            return true;
       // });
    }
}