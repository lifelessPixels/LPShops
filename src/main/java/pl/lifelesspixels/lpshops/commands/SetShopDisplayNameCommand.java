package pl.lifelesspixels.lpshops.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.lifelesspixels.lpshops.LPShops;
import pl.lifelesspixels.lpshops.data.ShopDefinition;
import pl.lifelesspixels.lpshops.data.ShopDefinitions;
import pl.lifelesspixels.lputilities.commands.AdvancedArgsParser;
import pl.lifelesspixels.lputilities.commands.CommandUtils;

public class SetShopDisplayNameCommand implements CommandExecutor {

    private final static String SET_SHOP_DISPLAY_NAME_PERMISSION = "lpshops.command.setshopdisplayname";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] unusedArgs) {
        if(!sender.hasPermission(SET_SHOP_DISPLAY_NAME_PERMISSION)) {
            CommandUtils.sendNoPermissionsMessage(sender, alias);
            return true;
        }

        AdvancedArgsParser argsParser = AdvancedArgsParser.fromLastSentCommand(sender);
        String[] args = argsParser.getRawArgs();

        if(args.length == 2) {
            ShopDefinitions shopDefinitions = LPShops.getInstance().getShopDefinitions();

            String shopIdentifier = args[0];
            if(!shopDefinitions.hasShopWithIdentifier(shopIdentifier)) {
                sender.sendMessage(ChatColor.RED + "Cannot find shop with identifier " + ChatColor.RESET + shopIdentifier);
                return true;
            }

            String newDisplayName = args[1];
            ShopDefinition shopDefinition = shopDefinitions.getShopWithIdentifier(shopIdentifier);

            if(shopDefinitions.isBeingEdited(shopDefinition) || shopDefinitions.isCurrentlyOpened(shopDefinition)) {
                sender.sendMessage(ChatColor.RED + "Shop with identifier " + ChatColor.RESET + shopIdentifier +
                        ChatColor.RED + " is currently edited or opened by other player");
                return true;
            }

            shopDefinition.setDisplayName(newDisplayName);
            sender.sendMessage(ChatColor.GREEN + "Shop with identifier " + ChatColor.RESET + shopIdentifier +
                    ChatColor.GREEN + " now has display name " + ChatColor.RESET + newDisplayName);
            return true;
        }

        CommandUtils.sendUsage(sender, alias, "<shop identifier> <new display name>");
        return true;
    }

}
