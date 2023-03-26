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

public class RemoveShopCommand implements CommandExecutor {

    private final static String REMOVE_SHOP_PERMISSION = "lpshops.command.removeshop";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if(!sender.hasPermission(REMOVE_SHOP_PERMISSION)) {
            CommandUtils.sendNoPermissionsMessage(sender, alias);
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "/" + alias + " command only supports being called as a player for now, sorry :(");
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

            ConfirmationGUI confirmationGUI = new ConfirmationGUI(
                    ChatColor.RED + "" + ChatColor.BOLD +
                            "Do you really want to remove " + ChatColor.RESET + ChatColor.WHITE +
                            shopIdentifier + ChatColor.RED + "" + ChatColor.BOLD + " shop?",
                    null,
                    () -> {
                        ShopDefinition shopDefinition = shopDefinitions.getShopWithIdentifier(shopIdentifier);
                        if(shopDefinitions.isBeingEdited(shopDefinition) || shopDefinitions.isCurrentlyOpened(shopDefinition)) {
                            sender.sendMessage(ChatColor.RED + "Shop with identifier " + ChatColor.RESET + shopIdentifier +
                                    ChatColor.RED + " is currently edited or opened by other player");
                            return;
                        }

                        shopDefinitions.removeShopWithIdentifier(shopIdentifier);
                        player.sendMessage(ChatColor.GREEN + "Shop " + ChatColor.RESET + shopIdentifier + ChatColor.GREEN +
                                " was successfully removed");
                    },
                    () -> {
                        player.sendMessage(ChatColor.RED + "Shop " + ChatColor.RESET + shopIdentifier + ChatColor.RED +
                                " was NOT REMOVED");
                    }
            );

            confirmationGUI.openFor(player);
            return true;
        }

        CommandUtils.sendUsage(sender, alias, "<shop identifier>");
        return true;
    }

}
