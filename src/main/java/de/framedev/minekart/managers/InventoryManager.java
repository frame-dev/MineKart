package de.framedev.minekart.managers;

/*
 * de.framedev.essentialsmin.managers
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 22.09.2020 19:27
 */

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {

    private String title;
    private int size;
    private Inventory inventory;

    public InventoryManager() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSize() {
        return size * 9;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setItem(int index, ItemStack itemStack) {
        if(inventory == null) return;
        inventory.setItem(index, itemStack);
    }

    public void setItem(int index, Material material) {
        if(inventory == null) return;
        inventory.setItem(index, new ItemStack(material));
    }

    public ItemStack getItem(int index) {
        if(inventory == null) return null;
        return inventory.getItem(index);
    }

    public void addItem(ItemStack itemStack) {
        if(inventory == null) return;
        this.inventory.addItem(itemStack);
    }

    public void addItem(Material material) {
        if(this.inventory == null) return;
        this.inventory.addItem(new ItemStack(material));
    }

    public InventoryManager create() {
        this.inventory = Bukkit.createInventory(null, getSize(), getTitle());
        return this;
    }

    public void fillNull() {
        int inventorySize = size * 9;
        for(int i = 0; i < inventorySize; i++) {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i,new SpecialItem(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build());
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
