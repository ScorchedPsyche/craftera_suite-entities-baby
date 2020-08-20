package com.github.scorchedpsyche.craftera_suite.entities.baby.listeners;

import com.github.scorchedpsyche.craftera_suite.entities.baby.utils.EntityUtil;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class TestListener implements Listener {
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
            // so that Name Tag is not spent and entity is not renamed
            event.setCancelled(true);

            Entity targetEntity = event.getRightClicked();

            // Is entity valid for conversion?
            if ( entityUtil.IsAgeableAndBreedable(targetEntity) )
            {
                // Valid entity. Convert to adult or baby?
                if ( !((Ageable) targetEntity).isAdult() )
                {
                    // ADULT

                    // Checks if it's already been converted
                    if( ((Breedable) targetEntity).getAgeLock() )
                    {
                        // AgeLock is true, convert it back to adult
                        ((Breedable) targetEntity).setAgeLock(false);
                        ((Ageable) targetEntity).setAdult();
                    }
                } else {
                    // BABY

                    // Attempts to convert entity into a Baby
                    ((Breedable) targetEntity).setAgeLock(true);
                    ((Ageable) targetEntity).setBaby();

                    if( ((Ageable) targetEntity).isAdult() ){
                        // Failed. Can't be a baby
                        ((Breedable) targetEntity).setAgeLock(false);
                    }
                }
            }

//            DataWatcher watcher = ((CraftAgeable) targetEntity).getHandle().getDataWatcher();
//
//            for (Player player : Bukkit.getOnlinePlayers()) {
//                PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(
//                        targetEntity.getEntityId(), // Entity ID
//                        watcher, // Data watcher which you can get by accessing a method in a NMS Entity class
//                        false // Send All
//                );
//
//                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//                sourcePlayer.sendRawMessage("SENT: " + player.getName() );
//            }
        }
    }
}