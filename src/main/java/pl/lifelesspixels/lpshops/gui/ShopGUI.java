package pl.lifelesspixels.lpshops.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import pl.lifelesspixels.lpchestgui.data.ChestGUIClickAction;
import pl.lifelesspixels.lpchestgui.data.ChestGUIClickActionBuilder;
import pl.lifelesspixels.lpchestgui.data.ChestGUIClickActionResult;
import pl.lifelesspixels.lpchestgui.data.ClickType;
import pl.lifelesspixels.lpchestgui.gui.ChestGUI;
import pl.lifelesspixels.lpeconomy.data.Currency;
import pl.lifelesspixels.lpeconomy.data.PlayerAccount;
import pl.lifelesspixels.lpshops.LPShops;
import pl.lifelesspixels.lpshops.data.ShopDefinition;
import pl.lifelesspixels.lpshops.data.ShopItem;
import pl.lifelesspixels.lputilities.heads.CustomHeads;

import java.util.*;

public class ShopGUI extends ChestGUI {

    private final static int ITEMS_PER_PAGE = 21;
    private final static List<Integer> ITEM_SLOTS;

    private final static ItemStack QUESTION_MARK_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM4ZWExZjUxZjI1M2ZmNTE0MmNhMTFhZTQ1MTkzYTRhZDhjM2FiNWU5YzZlZWM4YmE3YTRmY2I3YmFjNDAifX19");
    private final static ItemStack LEFT_ARROW_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19");
    private final static ItemStack RIGHT_ARROW_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==");

    static {
        ITEM_SLOTS = new ArrayList<>();
        for(int i = 19; i < 26; i++)
            ITEM_SLOTS.add(i);
        for(int i = 28; i < 35; i++)
            ITEM_SLOTS.add(i);
        for(int i = 37; i < 44; i++)
            ITEM_SLOTS.add(i);
    }

    private final ShopDefinition shopDefinition;
    private int currentPage = 0;

    private final ChestGUIClickAction previousPageAction;
    private final ChestGUIClickAction nextPageAction;

    public ShopGUI(ShopDefinition shopDefinition) {
        super(6, shopDefinition.getDisplayName());
        this.shopDefinition = shopDefinition;

        // mark as opened
        LPShops.getInstance().getShopDefinitions().markAsCurrentlyOpened(shopDefinition);

        // create persistent actions
        previousPageAction = new ChestGUIClickActionBuilder()
                .withLeftClickCallback(context -> {
                    currentPage--;
                    showCurrentPage();
                    return ChestGUIClickActionResult.RemainOpen;
                })
                .withShiftLeftClickCallback(context -> {
                    currentPage = 0;
                    showCurrentPage();
                    return ChestGUIClickActionResult.RemainOpen;
                }).build();

        nextPageAction = new ChestGUIClickActionBuilder()
                .withLeftClickCallback(context -> {
                    currentPage++;
                    showCurrentPage();
                    return ChestGUIClickActionResult.RemainOpen;
                })
                .withShiftLeftClickCallback(context -> {
                    currentPage = getPageCount() - 1;
                    showCurrentPage();
                    return ChestGUIClickActionResult.RemainOpen;
                }).build();
    }

    @Override
    public void onOpen(Player player) {
        showCurrentPage();
    }

    @Override
    public void onPlayerItemClicked(Player player, int slot, ClickType clickType) {
        ItemStack itemStack = player.getInventory().getItem(slot);
        if(itemStack == null)
            return;

        ShopItem selectedShopItem = null;
        for(int index = 0; index < shopDefinition.getSoldItemsCount(); index++) {
            ShopItem shopItem = shopDefinition.getSoldItem(index);
            if(shopItem.getItem().isSimilar(itemStack) && shopItem.canBeSold()) {
                selectedShopItem = shopItem;
                break;
            }
        }

        if(selectedShopItem == null) {
            player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            return;
        }

        int soldCount;
        if(clickType == ClickType.Left) {
            int newStackCount = itemStack.getAmount() - 1;
            if(newStackCount <= 0) {
                player.getInventory().setItem(slot, null);
            } else {
                itemStack.setAmount(newStackCount);
                player.getInventory().setItem(slot, itemStack);
            }

            PlayerAccount account = LPShops.getInstance().getEconomy().getPlayerAccounts().getAccountFor(player);
            account.addToDefaultCurrencyBalance(selectedShopItem.getSellPrice());
            soldCount = 1;
        } else if(clickType == ClickType.ShiftLeft) {
            int stackSize = itemStack.getAmount();
            player.getInventory().setItem(slot, null);

            PlayerAccount account = LPShops.getInstance().getEconomy().getPlayerAccounts().getAccountFor(player);
            account.addToDefaultCurrencyBalance(selectedShopItem.getSellPrice() * stackSize);
            soldCount = stackSize;
        } else {
            player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            return;
        }

        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        String itemName = (meta.hasDisplayName()) ? meta.getDisplayName() : itemNameFromMaterial(itemStack.getType());
        player.sendMessage(ChatColor.GREEN + "Successfully sold " + ChatColor.RESET +
                soldCount + "x " + itemName + ChatColor.GREEN + " from your inventory");
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    @Override
    public void onInventoryClosed(Player player) {
        LPShops.getInstance().getShopDefinitions().markAsNoLongerOpened(shopDefinition);
    }

    private int getPageCount() {
        return Math.max(1, ((shopDefinition.getSoldItemsCount() + (ITEMS_PER_PAGE - 1)) / ITEMS_PER_PAGE));
    }

    private void showCurrentPage() {
        setEmptySlotsForItems();
        setPageIndicationItem();
        resetSlot(27);
        resetSlot(35);

        // show page switchers
        if(currentPage > 0)
            setAction(27, previousPageAction, createPreviousPageItem(), Sound.UI_BUTTON_CLICK);
        if(currentPage + 1 < getPageCount())
            setAction(35, nextPageAction, createNextPageItem(), Sound.UI_BUTTON_CLICK);

        // show items
        List<ShopItem> pageItems = getItemsForPage(currentPage);
        int index = 0;
        while(index < pageItems.size()) {
            ShopItem item = pageItems.get(index);
            int slot = ITEM_SLOTS.get(index);
            setItemAction(item, slot, currentPage * ITEMS_PER_PAGE + index);

            index++;
        }
    }

    private void setEmptySlotsForItems() {
        for(int slot : ITEM_SLOTS) {
            resetSlot(slot);
            setDummyItem(slot, null);
        }
    }

    private List<ShopItem> getItemsForPage(int page) {
        if(page >= getPageCount())
            return List.of();

        List<ShopItem> items = new ArrayList<>();
        int startingIndex = page * ITEMS_PER_PAGE;
        int endingIndex = startingIndex + ITEMS_PER_PAGE;
        for(int index = startingIndex; index < endingIndex; index++) {
            if (index >= shopDefinition.getSoldItemsCount())
                break;
            items.add(shopDefinition.getSoldItem(index));
        }

        return items;
    }

    private ItemStack createPageIndicatorItem() {
        ItemStack pageIndicatorItem = QUESTION_MARK_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(pageIndicatorItem.getItemMeta());
        meta.setDisplayName(ChatColor.WHITE + "Page " + (currentPage + 1));
        pageIndicatorItem.setItemMeta(meta);
        return pageIndicatorItem;
    }

    private ItemStack createPreviousPageItem() {
        ItemStack previousPageItem = LEFT_ARROW_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(previousPageItem.getItemMeta());
        meta.setDisplayName(ChatColor.WHITE + "Previous page");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Left Click: " + ChatColor.RESET + "" + ChatColor.WHITE
                + "Go to Page " + currentPage);
        if(currentPage != 1) {
            lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Shift+Left Click: " + ChatColor.RESET + "" + ChatColor.WHITE
                    + "Go to first page");
        }
        meta.setLore(lore);
        previousPageItem.setItemMeta(meta);
        return previousPageItem;
    }

    private ItemStack createNextPageItem() {
        ItemStack nextPageItem = RIGHT_ARROW_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(nextPageItem.getItemMeta());
        meta.setDisplayName(ChatColor.WHITE + "Next page");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Left Click: " + ChatColor.RESET + "" + ChatColor.WHITE +
                "Go to Page " + (currentPage + 2));
        if(currentPage != getPageCount() - 2) {
            lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Shift+Left Click: " + ChatColor.RESET + "" + ChatColor.WHITE +
                    "Go to last page");
        }
        meta.setLore(lore);
        nextPageItem.setItemMeta(meta);
        return nextPageItem;
    }

    private void setPageIndicationItem() {
        resetSlot(13);
        setAction(13, new ChestGUIClickActionBuilder().build(), createPageIndicatorItem());
    }

    private void setItemAction(ShopItem item, int slot, int shopIndex) {
        setAction(slot,
                new ChestGUIClickActionBuilder()
                        .withLeftClickCallback(context -> {
                            if(!item.canBeBought()) {
                                context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                                return ChestGUIClickActionResult.RemainOpen;
                            }

                            PlayerAccount account = LPShops.getInstance().getEconomy().getPlayerAccounts().getAccountFor(context.getPlayer());
                            if(account.canAffordInDefaultCurrency(item.getBuyCost())) {
                                PlayerInventory inventory = context.getPlayer().getInventory();
                                HashMap<Integer, ItemStack> didNotFit = inventory.addItem(item.getItem().clone());
                                if(didNotFit.size() > 0) {
                                    context.getPlayer().sendMessage(ChatColor.RED + "This item will not fit into your inventory");
                                    context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                                } else {
                                    ItemMeta meta = Objects.requireNonNull(item.getItem().getItemMeta());
                                    String itemName = (meta.hasDisplayName()) ? meta.getDisplayName() : itemNameFromMaterial(item.getItem().getType());
                                    context.getPlayer().sendMessage(ChatColor.GREEN + "Successfully bought " + ChatColor.RESET + "1x " + itemName);
                                    context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                                    account.subtractFromDefaultCurrencyBalance(item.getBuyCost());
                                }
                            } else {
                                context.getPlayer().sendMessage(ChatColor.RED + "You cannot afford this item");
                                context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                            }

                            return ChestGUIClickActionResult.RemainOpen;
                        })
                        .withShiftLeftClickCallback(context -> {
                            if(!item.canBeBought()) {
                                context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                                return ChestGUIClickActionResult.RemainOpen;
                            }

                            int stackSize = item.getItem().getMaxStackSize();
                            if(stackSize == 1) {
                                context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                                return ChestGUIClickActionResult.RemainOpen;
                            }

                            PlayerAccount account = LPShops.getInstance().getEconomy().getPlayerAccounts().getAccountFor(context.getPlayer());
                            if(account.canAffordInDefaultCurrency(item.getBuyCost() * stackSize)) {
                                PlayerInventory inventory = context.getPlayer().getInventory();
                                ItemStack toAdd = item.getItem().clone();
                                toAdd.setAmount(stackSize);
                                HashMap<Integer, ItemStack> didNotFit = inventory.addItem(toAdd);

                                int howMuchDidNotFit = 0;
                                for(int key : didNotFit.keySet())
                                    howMuchDidNotFit += didNotFit.get(key).getAmount();
                                int boughtCount = stackSize - howMuchDidNotFit;

                                if(howMuchDidNotFit == stackSize) {
                                    context.getPlayer().sendMessage(ChatColor.RED + "This item stack will not fit into your inventory");
                                    context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                                } else {
                                    ItemMeta meta = Objects.requireNonNull(item.getItem().getItemMeta());
                                    String itemName = (meta.hasDisplayName()) ? meta.getDisplayName() : itemNameFromMaterial(item.getItem().getType());
                                    String notFitMessage = (howMuchDidNotFit > 0) ? ChatColor.YELLOW + " (" + howMuchDidNotFit + " items did not fit into your inventory)" : "";
                                    context.getPlayer().sendMessage(ChatColor.GREEN + "Successfully bought " +
                                            ChatColor.RESET + boughtCount + "x " + itemName + notFitMessage);
                                    context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                                    account.subtractFromDefaultCurrencyBalance(item.getBuyCost() * boughtCount);
                                }
                            } else {
                                context.getPlayer().sendMessage(ChatColor.RED + "You cannot afford this item stack");
                                context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                            }

                            return ChestGUIClickActionResult.RemainOpen;
                        })
                        .withShiftRightClickCallback(context -> {
                            if(!item.canBeSold()) {
                                context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                                return ChestGUIClickActionResult.RemainOpen;
                            }

                            PlayerInventory inventory = context.getPlayer().getInventory();
                            int soldCount = 0;
                            for(int inventorySlot = 0; inventorySlot < inventory.getSize(); inventorySlot++) {
                                ItemStack itemStack = inventory.getItem(inventorySlot);
                                if(itemStack == null)
                                    continue;;

                                if(!itemStack.isSimilar(item.getItem()))
                                    continue;

                                soldCount += itemStack.getAmount();
                                inventory.setItem(inventorySlot, null);
                            }

                            if(soldCount == 0) {
                                context.getPlayer().sendMessage(ChatColor.RED + "You don't have any such item in your inventory");
                                context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                            } else {
                                ItemMeta meta = Objects.requireNonNull(item.getItem().getItemMeta());
                                String itemName = (meta.hasDisplayName()) ? meta.getDisplayName() : itemNameFromMaterial(item.getItem().getType());
                                context.getPlayer().sendMessage(ChatColor.GREEN + "Successfully sold " + ChatColor.RESET +
                                        soldCount + "x " + itemName + ChatColor.GREEN + " from your inventory");
                                context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                            }

                            return ChestGUIClickActionResult.RemainOpen;
                        }).build(),
                getDecoratedShopItem(item),
                Sound.UI_BUTTON_CLICK);
    }

    private ItemStack getDecoratedShopItem(ShopItem shopItem) {
        ItemStack item = shopItem.getItem().clone();
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        ArrayList<String> lore = new ArrayList<>();
        List<String> originalLore = meta.getLore();
        if(originalLore != null)
            lore.addAll(originalLore);
        lore.add("");
        if(shopItem.canBeBought())
            lore.add(ChatColor.WHITE + "Current buy price: " + getBuyPriceText(shopItem.getBuyCost()));
        if(shopItem.canBeSold())
            lore.add(ChatColor.WHITE + "Current sell price: " + getSellPriceText(shopItem.getSellPrice()));
        lore.add("");
        if(shopItem.canBeBought()) {
            lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Left Click: " + ChatColor.RESET + ""
                    + ChatColor.WHITE + "Buy x1");

            int maxStackSize = shopItem.getItem().getMaxStackSize();
            if(maxStackSize > 1) {
                lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Shift+Left Click: " + ChatColor.RESET + ""
                        + ChatColor.WHITE + "Buy x" + maxStackSize);
            }
        }
        if(shopItem.canBeSold()) {
            lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Shift+Right Click: " + ChatColor.RESET + ""
                    + ChatColor.WHITE + "Sell all");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private String getBuyPriceText(long price) {
        Currency defaultCurrency = LPShops.getInstance().getEconomy().getCurrencies().getDefaultCurrency();

        if(price < 0)
            return ChatColor.RED + "" + ChatColor.BOLD + "Cannot be bought";
        return ChatColor.GOLD + "" + ChatColor.BOLD + price + " " + defaultCurrency.getReadableName();
    }

    private String getSellPriceText(long price) {
        Currency defaultCurrency = LPShops.getInstance().getEconomy().getCurrencies().getDefaultCurrency();

        if(price < 0)
            return ChatColor.RED + "" + ChatColor.BOLD + "Cannot be sold";
        return ChatColor.GOLD + "" + ChatColor.BOLD + price + " " + defaultCurrency.getReadableName();
    }

    private String itemNameFromMaterial(Material material) {
        String[] parts = material.toString().split("_");
        for(int index = 0; index < parts.length; index++) {
            String value = parts[index].toLowerCase();
            parts[index] = Character.toUpperCase(value.charAt(0)) + value.substring(1);
        }

        return String.join(" ", parts);
    }


}
