package pl.lifelesspixels.lpshops.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.lifelesspixels.lpshops.LPShops;
import pl.lifelesspixels.lpshops.data.ShopDefinition;
import pl.lifelesspixels.lpshops.data.ShopDefinitions;
import pl.lifelesspixels.lputilities.commands.CommandUtils;
import pl.lifelesspixels.lputilities.gui.ConfirmationGUI;

import java.util.Set;

public class ListShopsCommand implements CommandExecutor {

    private final static String LIST_SHOPS_PERMISSION = "lpshops.command.listshops";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if(!sender.hasPermission(LIST_SHOPS_PERMISSION)) {
            CommandUtils.sendNoPermissionsMessage(sender, alias);
            return true;
        }

        if(args.length == 0) {
            ShopDefinitions shopDefinitions = LPShops.getInstance().getShopDefinitions();
            Set<String> identifiers = shopDefinitions.getAllShopIdentifiers();
            if(identifiers.size() == 0) {
                sender.sendMessage(ChatColor.RED + "There are no shops in existence");
                return true;
            }

            String shopList = String.join(", ", identifiers.stream().map(identifier -> {
                ShopDefinition definition = shopDefinitions.getShopWithIdentifier(identifier);
                return definition.getIdentifier() + " (" + definition.getDisplayName() + ")";
            }).toList());
            sender.sendMessage(ChatColor.GREEN + "Existing shops: " + ChatColor.RESET + shopList);
            return true;
        }

        CommandUtils.sendUsage(sender, alias, "");
        return true;
    }

}
