package pl.lifelesspixels.lpshops.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.lifelesspixels.lpchestgui.data.ChestGUIClickAction;
import pl.lifelesspixels.lpchestgui.data.ChestGUIClickActionBuilder;
import pl.lifelesspixels.lpchestgui.data.ChestGUIClickActionResult;
import pl.lifelesspixels.lpchestgui.data.ClickType;
import pl.lifelesspixels.lpchestgui.gui.ChestGUI;
import pl.lifelesspixels.lpeconomy.data.Currency;
import pl.lifelesspixels.lpshops.LPShops;
import pl.lifelesspixels.lpshops.data.ShopDefinition;
import pl.lifelesspixels.lpshops.data.ShopItem;
import pl.lifelesspixels.lputilities.heads.CustomHeads;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShopItemEditGUI extends ChestGUI {

    private enum State {
        Main,
        EditingBuyPrice,
        EditingSellPrice
    }

    private final static int MAX_PRICE_LENGTH = 15;

    private final static ItemStack MISSING_TEXTURE_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M2MDNjNzk1NjAzMTk5OTZkNjM5NDEyOGI0OWZlYzc2NTBjZjg2N2ExZTQ4ZmI4MGM2MDQzZTc3MGRkNzFiZCJ9fX0=");
    private final static ItemStack UP_ARROW_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFkNmM4MWY4OTlhNzg1ZWNmMjZiZTFkYzQ4ZWFlMmJjZmU3NzdhODYyMzkwZjU3ODVlOTViZDgzYmQxNGQifX19");
    private final static ItemStack DOWN_ARROW_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODgyZmFmOWE1ODRjNGQ2NzZkNzMwYjIzZjg5NDJiYjk5N2ZhM2RhZDQ2ZDRmNjVlMjg4YzM5ZWI0NzFjZTcifX19");
    private static final ItemStack GREEN_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNlOWY0ZGJhZGRlMGY3MjdjNTgwM2Q3NWQ4YmIzNzhmYjlmY2I0YjYwZDMzYmVjMTkwOTJhM2EyZTdiMDdhOSJ9fX0=");
    private static final ItemStack RED_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjA2MmQ4ZDcyZjU4OTFjNzFmYWIzMGQ1MmUwNDgxNzk1YjNkMmQzZDJlZDJmOGI5YjUxN2Q3ZDI4MjFlMzVkNiJ9fX0=");
    private static final ItemStack RED_LEFT_ARROW_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0=");
    private static final ItemStack RED_C_LETTER = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg1MTRiMjFhZTljZGY1YTMzYjg4Mjk4ZWM1YTc3ZjgyMGI4NjllOTdjZDI0OGVlOTc5MWU0ZDMxNTYwN2UifX19");
    private static final ItemStack[] DIGITS = new ItemStack[10];
    private static final ItemStack KEYPAD_BACKSPACE;
    private static final ItemStack KEYPAD_CLEAR;
    private static final int[] KEYPAD_SLOTS = new int[] { 41, 13, 14, 15, 22, 23, 24, 31, 32, 33 };

    static {
        DIGITS[0] = CustomHeads.createHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2YwOTAxOGY0NmYzNDllNTUzNDQ2OTQ2YTM4NjQ5ZmNmY2Y5ZmRmZDYyOTE2YWVjMzNlYmNhOTZiYjIxYjUifX19");
        DIGITS[1] = CustomHeads.createHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2E1MTZmYmFlMTYwNThmMjUxYWVmOWE2OGQzMDc4NTQ5ZjQ4ZjZkNWI2ODNmMTljZjVhMTc0NTIxN2Q3MmNjIn19fQ==");
        DIGITS[2] = CustomHeads.createHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY5OGFkZDM5Y2Y5ZTRlYTkyZDQyZmFkZWZkZWMzYmU4YTdkYWZhMTFmYjM1OWRlNzUyZTlmNTRhZWNlZGM5YSJ9fX0=");
        DIGITS[3] = CustomHeads.createHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ5ZTRjZDVlMWI5ZjNjOGQ2Y2E1YTFiZjQ1ZDg2ZWRkMWQ1MWU1MzVkYmY4NTVmZTlkMmY1ZDRjZmZjZDIifX19");
        DIGITS[4] = CustomHeads.createHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJhM2Q1Mzg5ODE0MWM1OGQ1YWNiY2ZjODc0NjlhODdkNDhjNWMxZmM4MmZiNGU3MmY3MDE1YTM2NDgwNTgifX19");
        DIGITS[5] = CustomHeads.createHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDFmZTM2YzQxMDQyNDdjODdlYmZkMzU4YWU2Y2E3ODA5YjYxYWZmZDYyNDVmYTk4NDA2OTI3NWQxY2JhNzYzIn19fQ==");
        DIGITS[6] = CustomHeads.createHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2FiNGRhMjM1OGI3YjBlODk4MGQwM2JkYjY0Mzk5ZWZiNDQxODc2M2FhZjg5YWZiMDQzNDUzNTYzN2YwYTEifX19");
        DIGITS[7] = CustomHeads.createHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk3NzEyYmEzMjQ5NmM5ZTgyYjIwY2M3ZDE2ZTE2OGIwMzViNmY4OWYzZGYwMTQzMjRlNGQ3YzM2NWRiM2ZiIn19fQ==");
        DIGITS[8] = CustomHeads.createHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJjMGZkYTlmYTFkOTg0N2EzYjE0NjQ1NGFkNjczN2FkMWJlNDhiZGFhOTQzMjQ0MjZlY2EwOTE4NTEyZCJ9fX0=");
        DIGITS[9] = CustomHeads.createHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZhYmM2MWRjYWVmYmQ1MmQ5Njg5YzA2OTdjMjRjN2VjNGJjMWFmYjU2YjhiMzc1NWU2MTU0YjI0YTVkOGJhIn19fQ==");

        for(int i = 0; i < 10; i++) {
            ItemStack item = DIGITS[i];
            ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
            meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + i);
            item.setItemMeta(meta);
        }

        ItemStack item = RED_LEFT_ARROW_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Backspace");
        item.setItemMeta(meta);
        KEYPAD_BACKSPACE = item;

        item = RED_C_LETTER.clone();
        meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Clear");
        item.setItemMeta(meta);
        KEYPAD_CLEAR = item;
    }

    private ShopEditGUI guiToReopen;
    private ShopDefinition shopDefinition;
    private int index = -1;
    private ItemStack item = null;
    private long buyPrice = -1;
    private long sellPrice = -1;
    private State guiState = State.Main;
    private String priceEditText = "";
    private ChestGUIClickAction[] keypadActions = new ChestGUIClickAction[10];
    private ChestGUIClickAction backspaceAction = null;
    private ChestGUIClickAction clearAction = null;

    public ShopItemEditGUI(ShopEditGUI guiToReopen, ShopDefinition shopDefinition, int index) {
        super(6, "Item editor");
        this.shopDefinition = shopDefinition;
        this.guiToReopen = guiToReopen;
        this.index = index;

        if(index >= 0 && index < shopDefinition.getSoldItemsCount()) {
            ShopItem shopItem = shopDefinition.getSoldItem(index);
            item = shopItem.getItem();
            buyPrice = shopItem.getBuyCost();
            sellPrice = shopItem.getSellPrice();
        }

        createKeypadActions();
    }

    public ShopItemEditGUI(ShopEditGUI guiToReopen, ShopDefinition shopDefinition, ItemStack item) {
        this(guiToReopen, shopDefinition, -1);
        this.item = item;
    }

    @Override
    public void onOpen(Player player) {
        refreshGUI();
    }

    @Override
    public void onPlayerItemClicked(Player player, int slot, ClickType clickType) {
        if(clickType == ClickType.ShiftLeft) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if(itemStack == null)
                return;

            ItemStack clone = itemStack.clone();
            clone.setAmount(1);
            item = clone;
            refreshGUI();
        }
    }

    @Override
    public void onInventoryClosed(Player player) {
        guiToReopen.openFor(player);
    }

    private void refreshGUI() {
        resetAllSlots();

        switch (guiState) {
            case Main -> {
                // set item preview
                setDummyItem(13, createSoldItemPreviewItem());

                // set buy/sell prices actions
                setAction(37,
                        new ChestGUIClickActionBuilder()
                                .withLeftClickCallback(context -> {
                                    priceEditText = (buyPrice < 0) ? "" : Long.toString(buyPrice);
                                    guiState = State.EditingBuyPrice;
                                    refreshGUI();
                                    return ChestGUIClickActionResult.RemainOpen;
                                }).build(),
                        createSetBuyPriceItem(),
                        Sound.UI_BUTTON_CLICK);
                setAction(38,
                        new ChestGUIClickActionBuilder()
                                .withLeftClickCallback(context -> {
                                    priceEditText = (sellPrice < 0) ? "" : Long.toString(sellPrice);
                                    guiState = State.EditingSellPrice;
                                    refreshGUI();
                                    return ChestGUIClickActionResult.RemainOpen;
                                }).build(),
                        createSetSellPriceItem(),
                        Sound.UI_BUTTON_CLICK);

                // set save/cancel
                setItemSaveAction();
                setAction(43,
                        new ChestGUIClickActionBuilder()
                                .withLeftClickCallback(context -> {
                                    guiToReopen.openFor(context.getPlayer());
                                    return ChestGUIClickActionResult.RemainOpen;
                                }).build(),
                        createItemCancelItem(),
                        Sound.ENTITY_ITEM_BREAK);
            }

            case EditingBuyPrice, EditingSellPrice -> {
                // show price
                if(guiState == State.EditingBuyPrice)
                    setDummyItem(10, createBuyPriceEditItem());
                else setDummyItem(10, createSellPriceEditItem());

                // show keypad
                setKeypadItems();

                // set save/cancel
                setAction(37,
                        new ChestGUIClickActionBuilder()
                                .withLeftClickCallback(context -> {
                                    if(priceEditText.length() == 0) {
                                        if(guiState == State.EditingBuyPrice)
                                            buyPrice = -1;
                                        else sellPrice = -1;
                                        guiState = State.Main;
                                        refreshGUI();
                                        return ChestGUIClickActionResult.RemainOpen;
                                    }

                                    long value = Long.parseLong(priceEditText);
                                    if(guiState == State.EditingBuyPrice)
                                        buyPrice = value;
                                    else sellPrice = value;

                                    guiState = State.Main;
                                    refreshGUI();
                                    return ChestGUIClickActionResult.RemainOpen;
                                }).build(),
                        createPriceSaveItem(),
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                setAction(38,
                        new ChestGUIClickActionBuilder()
                                .withLeftClickCallback(context -> {
                                    guiState = State.Main;
                                    refreshGUI();
                                    return ChestGUIClickActionResult.RemainOpen;
                                }).build(),
                        createPriceCancelItem(),
                        Sound.ENTITY_ITEM_BREAK);
            }
        }
    }

    private void saveItem() {
        if(index >= 0 && index < shopDefinition.getSoldItemsCount())
            shopDefinition.setSoldItem(index, getShopItem());
        else shopDefinition.addSoldItem(getShopItem());
    }

    private ShopItem getShopItem() {
        return new ShopItem(item, buyPrice, sellPrice);
    }

    private ItemStack createSoldItemPreviewItem() {
        if(item == null) {
            ItemStack missingItem = MISSING_TEXTURE_HEAD.clone();
            ItemMeta meta = Objects.requireNonNull(missingItem.getItemMeta());
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "No item selected");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.WHITE + "To select an item, " + ChatColor.YELLOW + "" + ChatColor.BOLD +
                    "Shift+Left Click" + ChatColor.RESET + "" + ChatColor.WHITE + " it from");
            lore.add(ChatColor.WHITE + "your inventory.");
            meta.setLore(lore);
            missingItem.setItemMeta(meta);
            return missingItem;
        }

        ItemStack selectedItem = item.clone();
        ItemMeta meta = Objects.requireNonNull(selectedItem.getItemMeta());
        ArrayList<String> lore = new ArrayList<>();
        List<String> originalLore = meta.getLore();
        if(originalLore != null)
            lore.addAll(originalLore);
        lore.add("");
        lore.add(ChatColor.WHITE + "To select other item, " + ChatColor.YELLOW + "" + ChatColor.BOLD +
                "Shift+Left Click" + ChatColor.RESET + "" + ChatColor.WHITE + " it from");
        lore.add(ChatColor.WHITE + "your inventory.");
        meta.setLore(lore);
        selectedItem.setItemMeta(meta);
        return selectedItem;
    }

    private ItemStack createSetBuyPriceItem() {
        ItemStack setBuyPriceItem = UP_ARROW_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(setBuyPriceItem.getItemMeta());
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Edit buy price");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "Current buy price: " + getCurrentBuyPriceText());
        lore.add("");
        lore.add(ChatColor.WHITE + "Clicking this option will present you the numpad");
        lore.add(ChatColor.WHITE + "which can be used to enter new buy price or disallow");
        lore.add(ChatColor.WHITE + "buying this item completely.");
        meta.setLore(lore);
        setBuyPriceItem.setItemMeta(meta);
        return setBuyPriceItem;
    }

    private ItemStack createSetSellPriceItem() {
        ItemStack setSellPriceItem = DOWN_ARROW_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(setSellPriceItem.getItemMeta());
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Edit sell price");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "Current sell price: " + getCurrentSellPriceText());
        lore.add("");
        lore.add(ChatColor.WHITE + "Clicking this option will present you the numpad");
        lore.add(ChatColor.WHITE + "which can be used to enter new sell price or disallow");
        lore.add(ChatColor.WHITE + "selling this item completely.");
        meta.setLore(lore);
        setSellPriceItem.setItemMeta(meta);
        return setSellPriceItem;
    }

    private String getCurrentBuyPriceText() {
        if(buyPrice < 0)
            return ChatColor.RED + "" + ChatColor.BOLD + "Item cannot be bought";

        Currency defaultCurrency = LPShops.getInstance().getEconomy().getCurrencies().getDefaultCurrency();
        return ChatColor.GOLD + "" + ChatColor.BOLD + buyPrice + " " + defaultCurrency.getReadableName();
    }

    private String getCurrentSellPriceText() {
        if(sellPrice < 0)
            return ChatColor.RED + "" + ChatColor.BOLD + "Item cannot be sold";

        Currency defaultCurrency = LPShops.getInstance().getEconomy().getCurrencies().getDefaultCurrency();
        return ChatColor.GOLD + "" + ChatColor.BOLD + sellPrice + " " + defaultCurrency.getReadableName();
    }

    private ItemStack createItemCancelItem() {
        ItemStack cancelItem = RED_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(cancelItem.getItemMeta());
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cancel");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "The item will not be saved");
        meta.setLore(lore);
        cancelItem.setItemMeta(meta);
        return cancelItem;
    }

    private void setItemSaveAction() {
        List<String> inconsistencies = collectStateInconsistencies();
        if(inconsistencies == null) {
            setAction(42,
                    new ChestGUIClickActionBuilder()
                            .withLeftClickCallback(context -> {
                                if(index < 0)
                                    shopDefinition.addSoldItem(getShopItem());
                                else shopDefinition.setSoldItem(index, getShopItem());
                                guiToReopen.openFor(context.getPlayer());
                                return ChestGUIClickActionResult.RemainOpen;
                            }).build(),
                    createAllowedSaveItem(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            return;
        }

        ItemStack inconsistenciesItem = new ItemStack(Material.BARRIER);
        ItemMeta meta = Objects.requireNonNull(inconsistenciesItem.getItemMeta());
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cannot save item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "Errors found:");
        lore.addAll(inconsistencies);
        meta.setLore(lore);
        inconsistenciesItem.setItemMeta(meta);
        setDummyItem(42, inconsistenciesItem);
    }

    private ItemStack createAllowedSaveItem() {
        ItemStack saveItem = GREEN_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(saveItem.getItemMeta());
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Save item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        if(index < 0) {
            lore.add(ChatColor.WHITE + "The item will be added to " + ChatColor.YELLOW + ChatColor.BOLD +
                    shopDefinition.getIdentifier() + ChatColor.RESET + "" + ChatColor.WHITE + " shop");
        } else {
            lore.add(ChatColor.WHITE + "The item will be saved at index " + ChatColor.YELLOW + ChatColor.BOLD + index +
                    ChatColor.RESET + "" + ChatColor.WHITE + " in " + ChatColor.YELLOW + "" + ChatColor.BOLD +
                    shopDefinition.getIdentifier() + ChatColor.RESET + "" + ChatColor.WHITE + " shop");
        }
        meta.setLore(lore);
        saveItem.setItemMeta(meta);
        return saveItem;
    }

    private List<String> collectStateInconsistencies() {
        ArrayList<String> inconsistencies = new ArrayList<>();
        if(item == null)
            inconsistencies.add(ChatColor.WHITE + "  - Item must be selected");
        if(buyPrice < 0 && sellPrice < 0)
            inconsistencies.add(ChatColor.WHITE + "  - Item must be at least buyable or sellable");

        if(inconsistencies.size() == 0)
            return null;
        return inconsistencies;
    }

    private ItemStack createBuyPriceEditItem() {
        return createPriceEditItem("buy", "bought");
    }

    private ItemStack createSellPriceEditItem() {
        return createPriceEditItem("sell", "sold");
    }

    private ItemStack createPriceEditItem(String priceName, String pastString) {
        ItemStack priceEditItem = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = Objects.requireNonNull(priceEditItem.getItemMeta());
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Current " + priceName + " price: " +
                ChatColor.RESET + "" + ChatColor.WHITE + (priceEditText.length() == 0 ? "Cannot be " + pastString : priceEditText));
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "Use the keypad on the right to change the price");
        lore.add(ChatColor.WHITE + "or clear the price to forbid " + priceName + "ing this item.");
        meta.setLore(lore);
        priceEditItem.setItemMeta(meta);
        return priceEditItem;
    }

    private void createKeypadActions() {
        for(int i = 0; i < 10; i++) {
            final int digit = i;
            keypadActions[i] = new ChestGUIClickActionBuilder()
                    .withLeftClickCallback(context -> {
                        if((digit == 0 && priceEditText.length() == 0) || priceEditText.length() >= MAX_PRICE_LENGTH) {
                            context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                            return ChestGUIClickActionResult.RemainOpen;
                        }

                        priceEditText += Integer.toString(digit);
                        refreshGUI();
                        return ChestGUIClickActionResult.RemainOpen;
                    }).build();
        }

        backspaceAction = new ChestGUIClickActionBuilder()
                .withLeftClickCallback(context -> {
                    if(priceEditText.length() == 0) {
                        context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                        return ChestGUIClickActionResult.RemainOpen;
                    }

                    priceEditText = priceEditText.substring(0, priceEditText.length() - 1);
                    refreshGUI();
                    return ChestGUIClickActionResult.RemainOpen;
                }).build();

        clearAction = new ChestGUIClickActionBuilder()
                .withLeftClickCallback(context -> {
                    priceEditText = "";
                    refreshGUI();
                    return ChestGUIClickActionResult.RemainOpen;
                }).build();
    }

    private void setKeypadItems() {
        for(int i = 0; i < 10; i++)
            setAction(KEYPAD_SLOTS[i], keypadActions[i], DIGITS[i], Sound.UI_BUTTON_CLICK);

        setAction(16, backspaceAction, KEYPAD_BACKSPACE, Sound.UI_BUTTON_CLICK);
        setAction(25, clearAction, KEYPAD_CLEAR, Sound.UI_BUTTON_CLICK);
    }

    private ItemStack createPriceSaveItem() {
        ItemStack saveItem = GREEN_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(saveItem.getItemMeta());
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Save price");
        saveItem.setItemMeta(meta);
        return saveItem;
    }

    private ItemStack createPriceCancelItem() {
        ItemStack cancelItem = RED_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(cancelItem.getItemMeta());
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cancel");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "The price will not be saved");
        meta.setLore(lore);
        cancelItem.setItemMeta(meta);
        return cancelItem;
    }

}
