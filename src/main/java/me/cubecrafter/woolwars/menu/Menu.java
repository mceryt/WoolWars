package me.cubecrafter.woolwars.menu;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@RequiredArgsConstructor
public abstract class Menu implements InventoryHolder {

    protected final Player player;
    private Inventory inventory;
    public abstract String getTitle();
    public abstract int getRows();
    public abstract List<MenuItem> getItems();

    public void openMenu() {
        getItems().forEach(item -> getInventory().setItem(item.getSlot(), item.getItem()));
        player.openInventory(getInventory());
    }

    public void closeMenu() {
        player.closeInventory();
    }

    public void updateMenu() {
        getItems().forEach(item -> getInventory().setItem(item.getSlot(), item.getItem()));
    }

    public void addFiller(ItemStack filler, List<Integer> slots) {
        for (Integer slot : slots) {
            getInventory().setItem(slot, filler);
        }
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null) inventory = Bukkit.createInventory(this, getRows()*9, TextUtil.color(getTitle()));
        return inventory;
    }

}
