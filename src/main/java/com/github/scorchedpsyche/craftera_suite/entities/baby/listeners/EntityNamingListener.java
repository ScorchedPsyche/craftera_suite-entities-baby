package com.github.scorchedpsyche.craftera_suite.entities.baby.listeners;

import com.github.scorchedpsyche.craftera_suite.entities.baby.utils.EntityUtil;
import net.minecraft.server.v1_16_R2.DataWatcher;
import net.minecraft.server.v1_16_R2.DataWatcherRegistry;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftAgeable;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntityNamingListener implements Listener {
    private String Text_CraftEra_Suite = ChatColor.AQUA + "" + ChatColor.BOLD + "[CraftEra Suite] " + ChatColor.RESET;
    /**
     * Listens to player right-click interaction and checks if the target entity is renamed to "ces_baby/adult".
     * TO DO
     * @param event Player interaction event.
     * **/
    @EventHandler
    public void onPlayerInteractWithEntity(PlayerInteractEntityEvent event)
    {
        EntityUtil entityUtil = new EntityUtil();

        Player sourcePlayer = event.getPlayer();
        ItemStack mainHandItem =    sourcePlayer.getEquipment() != null ?
                                    sourcePlayer.getEquipment().getItemInMainHand() : null;

        // Checks if player is holding a name tag named "ces_baby/adult"
        if( mainHandItem != null && entityUtil.PlayerHoldsValidNameTag( mainHandItem ) )
        {
            // Player holds a named name tag. Must cancel the default Right-Click event
            event.setCancelled(true);

            Entity targetEntity = event.getRightClicked();

            // Is entity valid for conversion?
            if ( entityUtil.IsAgeableAndBreedable(targetEntity) )
            {
                // Valid entity. Convert to adult or baby?
                if ( !((Ageable) targetEntity).isAdult() )
                {
                    // Convert to Adult

                    // Checks if it's already been converted
                    if( ((Breedable) targetEntity).getAgeLock() )
                    {
                        // AgeLock is true, convert it back to adult
                        ((Breedable) targetEntity).setAgeLock(false);
                        ((Ageable) targetEntity).setAdult();

                        if( ((Ageable) targetEntity).isAdult() ){
                            // Successfully converted to Adult
                            sourcePlayer.sendRawMessage(
                                    Text_CraftEra_Suite + ChatColor.GREEN + targetEntity.getName() + ChatColor.RESET +
                                            " is now an adult!" );

//                                DataWatcher watcher = new DataWatcher(targetEntity);
//                                watcher.a(15, true); // 15 = isBaby
                        } else {
                            // Failed. Can't be an adult
                            sourcePlayer.sendRawMessage(
                                    Text_CraftEra_Suite + ChatColor.RED + targetEntity.getName() + ChatColor.RESET +
                                            " can't be an adult." );
                        }
                    } else {
                        // Hasn't been converted so the conversion is invalid.
                        sourcePlayer.sendRawMessage(
                                Text_CraftEra_Suite + ChatColor.RED + "This is a natural baby!" + ChatColor.RESET +
                                        " You can't convert Vanilla entities to Adult.");
                    }
                } else {
                    // Convert to Baby

                    // Attempts to convert entity into a Baby
                    ((Breedable) targetEntity).setAgeLock(true);
                    ((Ageable) targetEntity).setBaby();

                    if( !((Ageable) targetEntity).isAdult() ){
                        // Successfully converted to Baby
                        sourcePlayer.sendRawMessage(
                                Text_CraftEra_Suite + ChatColor.GREEN + targetEntity.getName() + ChatColor.RESET +
                                        " is now a baby!" );
                    } else {
                        // Failed. Can't be a baby
                        ((Breedable) targetEntity).setAgeLock(false);

                        sourcePlayer.sendRawMessage(
                                Text_CraftEra_Suite + ChatColor.RED + targetEntity.getName() + ChatColor.RESET +
                                        " can't be a baby." );
                    }
                }
            } else {
                // Invalid entity for conversion
                sourcePlayer.sendRawMessage(
                        Text_CraftEra_Suite + ChatColor.GREEN + targetEntity.getName() + ChatColor.RESET +
                                " is not valid for conversion!" );
            }

            DataWatcher watcher = ((CraftAgeable) targetEntity).getHandle().getDataWatcher();

            for (Player player : Bukkit.getOnlinePlayers()) {
                PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(
                        targetEntity.getEntityId(), // Entity ID
                        watcher, // Data watcher which you can get by accessing a method in a NMS Entity class
                        false // Send All
                );
                
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                sourcePlayer.sendRawMessage("SENT: " + player.getName() );
            }
        }
    }
}
