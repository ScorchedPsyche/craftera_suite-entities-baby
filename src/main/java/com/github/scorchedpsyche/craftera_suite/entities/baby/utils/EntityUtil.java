package com.github.scorchedpsyche.craftera_suite.entities.baby.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EntityUtil
{

    public boolean PlayerHoldsValidNameTag(ItemStack item)
    {
        // Checks if player is holding a name tag named "ces_baby"
        if(     item != null &&
                item.getType() == Material.NAME_TAG &&
                item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals("ces_baby/adult")
        )
        {
            return true;
        }

        // Not a valid item
        return false;
    }

    /**
     * Checks if entity is Ageable and Breedable
     * @param entity Target entity to be converted to baby.
     * @return True if the entity is both Ageable and Breedable
     */
    public boolean IsAgeableAndBreedable( Entity entity )
    {
        if(     entity instanceof Breedable &&
                entity instanceof Ageable )
        {
            // Entity is valid to become a baby
            return true;
        }

        // Entity is invalid for conversion
        return false;
    }
}
