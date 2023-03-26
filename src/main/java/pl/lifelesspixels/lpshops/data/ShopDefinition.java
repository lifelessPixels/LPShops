package pl.lifelesspixels.lpshops.data;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class ShopDefinition implements ConfigurationSerializable {

    private final String identifier;
    private String displayName = "Unnamed Shop";
    private List<ShopItem> soldItems = new ArrayList<>();

    ShopDefinition(String identifier) {
        this.identifier = identifier;
    }

    ShopDefinition(String identifier, String displayName, List<ShopItem> soldItems) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.soldItems = soldItems;
    }

    public int getSoldItemsCount() {
        return soldItems.size();
    }

    public ShopItem getSoldItem(int index) {
        return soldItems.get(index);
    }

    public void addSoldItem(ShopItem item) {
        soldItems.add(item);
    }

    public void removeSoldItem(int index) {
        soldItems.remove(index);
    }

    public void removeSoldItem(ShopItem item) {
        soldItems.remove(item);
    }

    public void clearSoldItems() {
        soldItems.clear();
    }

    public void setSoldItem(int index, ShopItem item) {
        soldItems.set(index, item);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializedValues = new HashMap<>();
        serializedValues.put("identifier", identifier);
        serializedValues.put("display-name", displayName);
        serializedValues.put("items", soldItems);
        return serializedValues;
    }

    public static ShopDefinition deserialize(Map<String, Object> values) {
        String identifier = Objects.requireNonNull((String)(values.get("identifier")));
        String displayName = Objects.requireNonNull((String)(values.get("display-name")));
        List<?> itemList = (List<?>)(values.get("items"));
        List<ShopItem> shopItems = new ArrayList<>();
        for(Object object : itemList) {
            if(object instanceof ShopItem)
                shopItems.add((ShopItem)(object));
        }
        return new ShopDefinition(identifier, displayName, shopItems);
    }

}
