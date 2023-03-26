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

public class CreateShopCommand implements CommandExecutor {

    private final static String CREATE_SHOP_PERMISSION = "lpshops.command.createshop";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] unusedArgs) {
        if(!sender.hasPermission(CREATE_SHOP_PERMISSION)) {
            CommandUtils.sendNoPermissionsMessage(sender, alias);
            return true;
        }

        AdvancedArgsParser argsParser = AdvancedArgsParser.fromLastSentCommand(sender);
        String[] args = argsParser.getRawArgs();

        if(args.length == 1 || args.length == 2) {
            ShopDefinitions shopDefinitions = LPShops.getInstance().getShopDefinitions();

            String shopIdentifier = args[0];
            for(char c : shopIdentifier.toCharArray()) {
                if(!Character.isLetterOrDigit(c)) {
                    sender.sendMessage(ChatColor.RED + "Identifier " + ChatColor.RESET + shopIdentifier + ChatColor.RED + " is not valid");
                    return true;
                }
            }

            String displayName = (args.length == 2) ? args[1] : shopIdentifier;
            if(shopDefinitions.hasShopWithIdentifier(shopIdentifier)) {
                sender.sendMessage(ChatColor.RED + "Shop with identifier " + ChatColor.RESET +
                        shopIdentifier + ChatColor.RED + " already exists");
                return true;
            }

            ShopDefinition newShop = shopDefinitions.createShop(shopIdentifier, displayName);
            sender.sendMessage(ChatColor.GREEN + "Shop with identifier " + ChatColor.RESET + shopIdentifier +
                    ChatColor.GREEN + " was successfully created");
            return true;
        }

        CommandUtils.sendUsage(sender, alias, "<shop identifier> [shop display name]");
        return true;
    }

}
