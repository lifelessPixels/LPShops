package pl.lifelesspixels.lpshops.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.lifelesspixels.lpshops.LPShops;
import pl.lifelesspixels.lputilities.commands.CommandUtils;

public class SaveShopsCommand implements CommandExecutor {

    private final static String SAVE_SHOPS_PERMISSION = "lpshops.command.saveshops";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if(!sender.hasPermission(SAVE_SHOPS_PERMISSION)) {
            CommandUtils.sendNoPermissionsMessage(sender, alias);
            return true;
        }

        if(args.length == 0) {
            try {
                LPShops.getInstance().getShopDefinitions().saveToFile();
                sender.sendMessage(ChatColor.GREEN + "Successfully saved shops");
            } catch (Exception e) { sender.sendMessage(ChatColor.RED + "Could not save shops, file access error occurred"); }
            return true;
        }

        CommandUtils.sendUsage(sender, alias, "");
        return true;
    }
}
