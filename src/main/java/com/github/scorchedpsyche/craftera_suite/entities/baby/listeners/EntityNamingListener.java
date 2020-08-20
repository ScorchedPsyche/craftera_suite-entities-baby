package com.github.scorchedpsyche.craftera_suite.entities.baby.listeners;

import com.github.scorchedpsyche.craftera_suite.entities.baby.utils.EntityUtil;
import net.minecraft.server.v1_16_R2.DataWatcher;
import net.minecraft.server.v1_16_R2.ItemCooldown;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftAgeable;
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
    private final String Text_CraftEra_Suite = ChatColor.AQUA + "" + ChatColor.BOLD + "[CraftEra Suite] " +
                    ChatColor.RESET;
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
        if(     mainHandItem != null &&
                entityUtil.PlayerHoldsValidNameTag( mainHandItem )
        )
        {
            // Player holds a named name tag. Must cancel the default Right-Click event
            // as to not spend the Name Tag and not rename the entity
            event.setCancelled(true);

            if( sourcePlayer.getCooldown(Material.NAME_TAG) == 0 )
            {
                // Add cooldown to Name Tag as to avoid spam
                sourcePlayer.setCooldown(Material.NAME_TAG, 5);

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

                            if( ((Ageable) targetEntity).isAdult() ){
                                // Successfully converted to Adult
//                            sourcePlayer.sendRawMessage(
//                                    Text_CraftEra_Suite + ChatColor.GREEN + targetEntity.getName() +
//                                            ChatColor.RESET + " is now an adult!" );

                                entityUtil.SpawnParticleAtEntity(
                                        targetEntity, Particle.DAMAGE_INDICATOR, 10, 0.0001);
                            } else {
                                // Failed. Can't be an adult
                                sourcePlayer.sendRawMessage(
                                        Text_CraftEra_Suite + ChatColor.RED + targetEntity.getName() + ChatColor.RESET +
                                                " can't be an adult." );

                                entityUtil.SpawnParticleAtEntity(
                                        targetEntity, Particle.EXPLOSION_NORMAL,3, 0.01 );
                            }
                        } else {
                            // Hasn't been converted so the conversion is invalid.
                            sourcePlayer.sendRawMessage(
                                    Text_CraftEra_Suite + ChatColor.RED + "This is a natural baby!" + ChatColor.RESET +
                                            " You can't convert Vanilla entities to Adult.");

                            entityUtil.SpawnParticleAtEntity(
                                    targetEntity, Particle.EXPLOSION_NORMAL,3, 0.01 );
                        }
                    } else {
                        // BABY

                        // Attempts to convert entity into a Baby
                        ((Breedable) targetEntity).setAgeLock(true);
                        ((Ageable) targetEntity).setBaby();

                        if( !((Ageable) targetEntity).isAdult() ){
                            // Conversion was successful
                            entityUtil.SpawnParticleAtEntity(targetEntity, Particle.HEART, 15);
                        } else {
                            // Failed. Can't be a baby
                            ((Breedable) targetEntity).setAgeLock(false);

                            sourcePlayer.sendRawMessage(
                                    Text_CraftEra_Suite + ChatColor.RED + targetEntity.getName() + ChatColor.RESET +
                                            " can't be a baby." );

                            entityUtil.SpawnParticleAtEntity(
                                    targetEntity, Particle.EXPLOSION_NORMAL,3, 0.01 );
                        }
                    }

                    DataWatcher watcher = ((CraftAgeable) targetEntity).getHandle().getDataWatcher();
                    PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(
                            targetEntity.getEntityId(), // Entity ID
                            watcher, // Data watcher which you can get by accessing a method in a NMS Entity class
                            false // Send All
                    );

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    }
                } else {
                    // Invalid entity for conversion
                    sourcePlayer.sendRawMessage(
                            Text_CraftEra_Suite + ChatColor.RED + targetEntity.getName() + ChatColor.RESET +
                                    " is not valid for conversion!" );


                    entityUtil.SpawnParticleAtEntity(
                            targetEntity, Particle.EXPLOSION_NORMAL,3, 0.01 );
                }
            }
        }
    }
}
