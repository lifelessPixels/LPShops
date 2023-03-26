package pl.lifelesspixels.lpshops.data;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ShopItem implements ConfigurationSerializable {

    private final ItemStack item;

    private final boolean canBeBought;
    private final long buyCost;

    private final boolean canBeSold;
    private final long sellPrice;

    public ShopItem(ItemStack item, long buyCost, long sellPrice) {
        this.item = item;
        this.canBeBought = buyCost >= 0;
        this.buyCost = buyCost;
        this.canBeSold = sellPrice >= 0;
        this.sellPrice = sellPrice;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializedValues = new HashMap<>();
        serializedValues.put("buy-cost", buyCost);
        serializedValues.put("sell-price", sellPrice);
        serializedValues.put("item", item);
        return serializedValues;
    }

    public static ShopItem deserialize(Map<String, Object> values) {
        ItemStack item = (ItemStack)(values.get("item"));
        long buyCost = ((Number)(values.get("buy-cost"))).longValue();
        long sellPrice = ((Number)(values.get("sell-price"))).longValue();
        return new ShopItem(item, buyCost, sellPrice);
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean canBeBought() {
        return canBeBought;
    }

    public long getBuyCost() {
        return buyCost;
    }

    public boolean canBeSold() {
        return canBeSold;
    }

    public long getSellPrice() {
        return sellPrice;
    }

}
