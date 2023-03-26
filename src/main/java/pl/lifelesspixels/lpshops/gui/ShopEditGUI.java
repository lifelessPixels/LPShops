package pl.lifelesspixels.lpshops.gui;

import org.bukkit.ChatColor;
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
import pl.lifelesspixels.lputilities.gui.ConfirmationGUI;
import pl.lifelesspixels.lputilities.heads.CustomHeads;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShopEditGUI extends ChestGUI {

    private final static int ITEMS_PER_PAGE = 21;
    private final static List<Integer> ITEM_SLOTS;

    private final static ItemStack QUESTION_MARK_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM4ZWExZjUxZjI1M2ZmNTE0MmNhMTFhZTQ1MTkzYTRhZDhjM2FiNWU5YzZlZWM4YmE3YTRmY2I3YmFjNDAifX19");
    private final static ItemStack LEFT_ARROW_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19");
    private final static ItemStack RIGHT_ARROW_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==");
    private final static ItemStack GREEN_PLUS_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
    private final static ItemStack RED_C_LETTER_HEAD = CustomHeads.createHeadFromBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg1MTRiMjFhZTljZGY1YTMzYjg4Mjk4ZWM1YTc3ZjgyMGI4NjllOTdjZDI0OGVlOTc5MWU0ZDMxNTYwN2UifX19");

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

    public ShopEditGUI(ShopDefinition shopDefinition) {
        super(6, "Editing: '" + shopDefinition.getIdentifier() + "' (" + shopDefinition.getDisplayName() + ")");

        this.shopDefinition = shopDefinition;
        LPShops.getInstance().getShopDefinitions().markAsCurrentlyEdited(shopDefinition);

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

        // show persistent buttons
        setAction(10,
                new ChestGUIClickActionBuilder()
                        .withLeftClickCallback(context -> {
                            ShopItemEditGUI itemEditGUI = new ShopItemEditGUI(this, shopDefinition, null);
                            itemEditGUI.openFor(context.getPlayer());
                            return ChestGUIClickActionResult.RemainOpen;
                        }).build(),
                createAddItemItem(),
                Sound.UI_BUTTON_CLICK);
        setAction(11,
                new ChestGUIClickActionBuilder()
                        .withLeftClickCallback(context -> {
                            ConfirmationGUI confirmationGUI =
                                    new ConfirmationGUI(ChatColor.RED + "" + ChatColor.BOLD +
                                            "Do you really want to clear " + ChatColor.RESET + ChatColor.WHITE +
                                            shopDefinition.getIdentifier() + ChatColor.RED + ChatColor.BOLD +
                                            " shop?",
                                            null,
                                            () -> {
                                                shopDefinition.clearSoldItems();
                                                currentPage = 0;
                                                this.openFor(context.getPlayer());
                                            },
                                            () -> this.openFor(context.getPlayer()));

                            confirmationGUI.openFor(context.getPlayer());
                            return ChestGUIClickActionResult.RemainOpen;
                        })
                        .build(),
                createClearShopItem(),
                Sound.UI_BUTTON_CLICK);
    }

    @Override
    public void onOpen(Player player) {
        showCurrentPage();
    }

    @Override
    public void onPlayerItemClicked(Player player, int slot, ClickType clickType) {
        if(clickType == ClickType.ShiftLeft) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if(itemStack == null)
                return;

            ItemStack clone = itemStack.clone();
            clone.setAmount(1);
            ShopItemEditGUI editGUI = new ShopItemEditGUI(this, shopDefinition, clone);
            editGUI.openFor(player);
        }
    }

    @Override
    public void onInventoryClosed(Player player) {
        LPShops.getInstance().getShopDefinitions().markAsNotEdited(shopDefinition);
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

    private ItemStack createAddItemItem() {
        ItemStack addItemItem = GREEN_PLUS_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(addItemItem.getItemMeta());
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Add item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "This action allows you to add new item to this shop.");
        lore.add("");
        lore.add(ChatColor.WHITE + "New GUI will be displayed in which you will be able to");
        lore.add(ChatColor.WHITE + "select item from your inventory which you want to add,");
        lore.add(ChatColor.WHITE + "as well as buy/sell prices.");
        lore.add("");
        lore.add(ChatColor.WHITE + "You can also " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Shift+Left Click"
                + ChatColor.RESET + "" + ChatColor.WHITE + " an item");
        lore.add(ChatColor.WHITE + "from your inventory to open the same editor with item");
        lore.add(ChatColor.WHITE + "already selected.");
        meta.setLore(lore);
        addItemItem.setItemMeta(meta);
        return addItemItem;
    }

    private ItemStack createClearShopItem() {
        ItemStack clearShopItem = RED_C_LETTER_HEAD.clone();
        ItemMeta meta = Objects.requireNonNull(clearShopItem.getItemMeta());
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Clear whole shop");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "This action will remove all items from current shop.");
        lore.add("");
        lore.add(ChatColor.WHITE + "Confirmation window will be displayed to avoid");
        lore.add(ChatColor.WHITE + "accidental shop deletions.");
        meta.setLore(lore);
        clearShopItem.setItemMeta(meta);
        return clearShopItem;
    }

    private void setPageIndicationItem() {
        resetSlot(13);
        setAction(13, new ChestGUIClickActionBuilder().build(), createPageIndicatorItem());
    }

    private void setItemAction(ShopItem item, int slot, int shopIndex) {
        setAction(slot,
                new ChestGUIClickActionBuilder()
                        .withLeftClickCallback(context -> {
                            ShopItemEditGUI itemEditGUI = new ShopItemEditGUI(this, shopDefinition, shopIndex);
                            itemEditGUI.openFor(context.getPlayer());
                            return ChestGUIClickActionResult.RemainOpen;
                        })
                        .withShiftRightClickCallback(context -> {
                            shopDefinition.removeSoldItem(shopIndex);
                            if(currentPage >= getPageCount())
                                currentPage -= 1;
                            showCurrentPage();
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
        lore.add(ChatColor.WHITE + "Current buy price: " + getBuyPriceText(shopItem.getBuyCost()));
        lore.add(ChatColor.WHITE + "Current sell price: " + getSellPriceText(shopItem.getSellPrice()));
        lore.add("");
        lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Left Click: " + ChatColor.RESET + ""
                + ChatColor.WHITE + "Edit this item");
        lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Shift+Right Click: " + ChatColor.RESET + ""
                + ChatColor.WHITE + "Remove this item");
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

}
