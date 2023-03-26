package pl.lifelesspixels.lpshops;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.lifelesspixels.lpchestgui.LPChestGUI;
import pl.lifelesspixels.lpeconomy.LPEconomy;
import pl.lifelesspixels.lpshops.commands.*;
import pl.lifelesspixels.lpshops.data.ShopDefinition;
import pl.lifelesspixels.lpshops.data.ShopDefinitions;
import pl.lifelesspixels.lpshops.data.ShopItem;

import java.util.Objects;

public class LPShops extends JavaPlugin {

    private static LPShops instance;

    private LPEconomy economy;
    private LPChestGUI chestGUI;
    private ShopDefinitions shopDefinitions;

    @Override
    public void onEnable() {
        instance = this;

        // get dependencies
        economy = LPEconomy.getInstance();
        chestGUI = LPChestGUI.getInstance();

        // register config serializers
        ConfigurationSerialization.registerClass(ShopDefinition.class);
        ConfigurationSerialization.registerClass(ShopItem.class);

        // load shop definitions
        shopDefinitions = new ShopDefinitions();
        shopDefinitions.loadFromFile();

        // register command handlers
        Objects.requireNonNull(getCommand("createshop")).setExecutor(new CreateShopCommand());
        Objects.requireNonNull(getCommand("removeshop")).setExecutor(new RemoveShopCommand());
        Objects.requireNonNull(getCommand("listshops")).setExecutor(new ListShopsCommand());
        Objects.requireNonNull(getCommand("editshop")).setExecutor(new EditShopCommand());
        Objects.requireNonNull(getCommand("setshopdisplayname")).setExecutor(new SetShopDisplayNameCommand());
        Objects.requireNonNull(getCommand("saveshops")).setExecutor(new SaveShopsCommand());
        Objects.requireNonNull(getCommand("openshop")).setExecutor(new OpenShopCommand());
    }

    @Override
    public void onDisable() {
        // write shop definitions to the file
        shopDefinitions.saveToFile();
    }

    public LPEconomy getEconomy() {
        return economy;
    }

    public LPChestGUI getChestGUI() {
        return chestGUI;
    }

    public ShopDefinitions getShopDefinitions() {
        return shopDefinitions;
    }

    public static LPShops getInstance() {
        return instance;
    }

}
