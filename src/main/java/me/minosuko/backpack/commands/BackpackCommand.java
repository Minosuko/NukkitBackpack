package me.minosuko.backpack.commands;

import cn.nukkit.Player;
import cn.nukkit.IPlayer;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import me.minosuko.backpack.Backpack;

public class BackpackCommand extends PluginCommand<Backpack> {
    public BackpackCommand(Backpack plugin) {
		super("backpack", plugin);
		this.setAliases(new String[]{ "bp" });
		this.setUsage("/backpack <player>");
		this.setDescription("Open backpack");
		commandParameters.clear();
		commandParameters.put("default", new CommandParameter[]{
			new CommandParameter("player", CommandParamType.TARGET, false)
		});
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only Ingame!");
            return false;
        }
		Player p = (Player) sender;
		IPlayer target = p;
		if (args.length == 1) {
            if (!p.hasPermission("command.backpack.view.other")) {
                p.sendMessage("You have no permission to open other player backpack");
                return false;
            }
			target = Server.getInstance().getOfflinePlayer(args[0]);
		}
        if (args.length > 1) {
			if (!p.hasPermission("command.backpack.view.other")) {
				sender.sendMessage("Usage: /backpack <player>");
			}else{
				sender.sendMessage("Usage: /backpack");
			}
            return false;
        }
		
		this.getPlugin().getManager().showBackpack(p, target);
        return false;
    }
}
