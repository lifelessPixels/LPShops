package pl.lifelesspixels.lpshops.data;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.lifelesspixels.lpshops.LPShops;

import java.io.File;
import java.util.*;

public class ShopDefinitions {

    private static final String SHOPS_FILE = "shops.yml";

    private HashMap<String, ShopDefinition> shopDefinitions = new HashMap<>();
    private final HashSet<ShopDefinition> currentlyEdited = new HashSet<>();
    private final HashSet<ShopDefinition> currentlyOpened = new HashSet<>();

    public void markAsCurrentlyEdited(ShopDefinition shopDefinition) {
        currentlyEdited.add(shopDefinition);
    }

    public void markAsNotEdited(ShopDefinition shopDefinition) {
        currentlyEdited.remove(shopDefinition);
    }

    public boolean isBeingEdited(ShopDefinition shopDefinition) {
        return currentlyEdited.contains(shopDefinition);
    }

    public void markAsCurrentlyOpened(ShopDefinition shopDefinition) {
        currentlyOpened.add(shopDefinition);
    }

    public void markAsNoLongerOpened(ShopDefinition shopDefinition) {
        currentlyOpened.remove(shopDefinition);
    }

    public boolean isCurrentlyOpened(ShopDefinition shopDefinition) {
        return currentlyOpened.contains(shopDefinition);
    }

    public boolean hasShopWithIdentifier(String identifier) {
        return shopDefinitions.containsKey(identifier);
    }

    public ShopDefinition getShopWithIdentifier(String identifier) {
        return shopDefinitions.get(identifier);
    }

    public ShopDefinition createShop(String identifier) {
        if(shopDefinitions.containsKey(identifier)) {
            throw new IllegalArgumentException("cannot create shop with identifier " + identifier +
                    ", shop with this identifier already exists");
        }

        ShopDefinition shop = new ShopDefinition(identifier);
        shopDefinitions.put(identifier, shop);
        return shop;
    }

    public ShopDefinition createShop(String identifier, String displayName) {
        ShopDefinition shop = createShop(identifier);
        shop.setDisplayName(displayName);
        return shop;
    }

    public void removeShopWithIdentifier(String identifier) {
        shopDefinitions.remove(identifier);
    }

    public Set<String> getAllShopIdentifiers() {
        return shopDefinitions.keySet();
    }

    public void loadFromFile() {
        File dataFolder = LPShops.getInstance().getDataFolder();
        if(!dataFolder.exists())
            return;

        File shopsFile = new File(dataFolder, SHOPS_FILE);
        if(!shopsFile.exists())
            return;

        YamlConfiguration storageFile = Objects.requireNonNull(YamlConfiguration.loadConfiguration(shopsFile));
        List<?> shopsList = storageFile.getList("shops");
        if(shopsList == null)
            return;

        List<ShopDefinition> shopDefinitions = new ArrayList<>();
        for(Object object : shopsList) {
            if(object instanceof ShopDefinition)
                shopDefinitions.add((ShopDefinition)(object));
        }

        HashMap<String, ShopDefinition> map = new HashMap<>();
        for(ShopDefinition definition : shopDefinitions) {
            if(map.containsKey(definition.getIdentifier()))
                return;

            map.put(definition.getIdentifier(), definition);
        }
        this.shopDefinitions = map;
    }

    public void saveToFile() {
        File dataFolder = LPShops.getInstance().getDataFolder();
        if(!dataFolder.exists() && !dataFolder.mkdir()) {
            LPShops.getInstance().getLogger().severe("cannot create " + dataFolder + " directory");
            return;
        }

        File shopsFile = new File(dataFolder, SHOPS_FILE);
        FileConfiguration configuration = new YamlConfiguration();
        configuration.set("shops", shopDefinitions.values().stream().toList());
        try { configuration.save(shopsFile); }
        catch (Exception e) {
            LPShops.getInstance().getLogger().severe("cannot write shop definitions to " + SHOPS_FILE + " file");
            e.printStackTrace();
        }
    }

}
