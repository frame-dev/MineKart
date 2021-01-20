package de.framedev.minekart.managers;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 11.07.2020 10:10
 */
public class SpecialItem {

    private String displayName;
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    private List<String> lore;

    public SpecialItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public SpecialItem(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public SpecialItem setDisplayName(String displayName) {
        this.displayName = displayName;
        itemMeta.setDisplayName(displayName);
        return this;
    }

    public Material getType() {
        return itemStack.getType();
    }

    public List<String> getLore() {
        return lore;
    }

    public SpecialItem setLore(String... lore) {
        this.lore = Arrays.asList(lore);
        this.itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public SpecialItem setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public SpecialItem hideFlag(ItemFlag itemFlag) {
        this.itemMeta.addItemFlags(itemFlag);
        return this;
    }

    public SpecialItem hideEnchantments() {
        this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public SpecialItem addEnchantment(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return itemMeta.hasEnchant(enchantment);
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean hasFlag(ItemFlag itemFlag) {
        return itemMeta.hasItemFlag(itemFlag);
    }

    public ItemStack build() {
        if (itemMeta != null) {
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        return itemStack;
    }
}
