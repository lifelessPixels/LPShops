package pl.lifelesspixels.lpshops.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.lifelesspixels.lpshops.LPShops;
import pl.lifelesspixels.lpshops.data.ShopDefinition;
import pl.lifelesspixels.lpshops.data.ShopDefinitions;
import pl.lifelesspixels.lpshops.gui.ShopGUI;
import pl.lifelesspixels.lputilities.commands.CommandUtils;

public class OpenShopCommand implements CommandExecutor {

    private final static String OPEN_SHOP_PERMISSION = "lpshops.command.openshop";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if(!sender.hasPermission(OPEN_SHOP_PERMISSION)) {
            CommandUtils.sendNoPermissionsMessage(sender, alias);
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "/" + alias + " command only can only be used as a player");
            return true;
        }
        Player player = (Player)(sender);

        if(args.length == 1) {
            ShopDefinitions shopDefinitions = LPShops.getInstance().getShopDefinitions();

            String shopIdentifier = args[0];
            if(!shopDefinitions.hasShopWithIdentifier(shopIdentifier)) {
                player.sendMessage(ChatColor.RED + "Cannot find shop with identifier " + ChatColor.RESET + shopIdentifier);
                return true;
            }

            ShopDefinition shopDefinition = shopDefinitions.getShopWithIdentifier(shopIdentifier);
            if(shopDefinitions.isBeingEdited(shopDefinition)) {
                sender.sendMessage(ChatColor.RED + "Shop with identifier " + ChatColor.RESET + shopIdentifier +
                        ChatColor.RED + " is currently edited and cannot be opened");
                return true;
            }

            ShopGUI shopGUI = new ShopGUI(shopDefinition);
            shopGUI.openFor(player);
            return true;
        }

        CommandUtils.sendUsage(sender, alias, "<shop identifier>");
        return true;
    }

}
