package de.framedev.minekart.managers;

import de.framedev.minekart.main.Main;
import org.bukkit.Material;

import java.util.ArrayList;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 20.07.2020 19:37
 */
public class SpecialItems {

    private final ArrayList<SpecialItem> specialItems;

    public SpecialItems() {
        this.specialItems = Main.getInstance().getSpecialItems();
    }

    public ArrayList<SpecialItem> getSpecialitems() {
        return specialItems;
    }

    public boolean addSpecialItem(SpecialItem specialItem) {
        if(!specialItems.contains(specialItem)) {
            specialItems.add(specialItem);
            return true;
        }
        return false;
    }

    public boolean removeSpecialItem(SpecialItem specialItem) {
        if(specialItems.contains(specialItem)) {
            specialItems.remove(specialItem);
            return true;
        }
        return false;
    }

    public SpecialItem createSpecialItem(Material material, String displayName) {
        return new SpecialItem(material).setDisplayName(displayName).setAmount(1);
    }
}
