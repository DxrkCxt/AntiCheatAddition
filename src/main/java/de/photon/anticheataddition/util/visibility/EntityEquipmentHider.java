package de.photon.anticheataddition.util.visibility;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.photon.anticheataddition.AntiCheatAddition;
import de.photon.anticheataddition.ServerVersion;
import de.photon.anticheataddition.protocol.packetwrappers.sentbyserver.equipment.IWrapperPlayEquipment;
import de.photon.anticheataddition.util.inventory.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

final class EntityEquipmentHider extends EntityInformationHider
{
    public EntityEquipmentHider()
    {
        super(PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    @Override
    protected void onHide(@NotNull Player observer, @NotNull Set<Entity> toHide)
    {
        Bukkit.getScheduler().runTask(AntiCheatAddition.getInstance(), () -> {
            for (Entity entity : toHide) IWrapperPlayEquipment.clearAllSlots(entity.getEntityId(), observer);
        });
    }

    @Override
    protected void onReveal(@NotNull Player observer, @NotNull Set<Entity> revealed)
    {
        final List<Player> observerList = List.of(observer);
        for (Entity entity : revealed) {
            if (entity instanceof Player) {
                final Player watched = (Player) entity;

                // Basic wrapper stuff.
                final IWrapperPlayEquipment wrapper = IWrapperPlayEquipment.of();
                wrapper.setEntityID(watched.getEntityId());

                // Hand Stuff.
                final List<ItemStack> handItems = InventoryUtil.getHandContents(watched);
                wrapper.setSlotStackPair(EnumWrappers.ItemSlot.MAINHAND, handItems.get(0));
                if (handItems.size() > 1) wrapper.setSlotStackPair(EnumWrappers.ItemSlot.OFFHAND, handItems.get(1));

                // Equipment stuff.
                wrapper.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, watched.getInventory().getHelmet());
                wrapper.setSlotStackPair(EnumWrappers.ItemSlot.CHEST, watched.getInventory().getChestplate());
                wrapper.setSlotStackPair(EnumWrappers.ItemSlot.LEGS, watched.getInventory().getLeggings());
                wrapper.setSlotStackPair(EnumWrappers.ItemSlot.FEET, watched.getInventory().getBoots());

                // Send the packet.
                wrapper.sendTranslatedPackets(observer);
            } else {
                ProtocolLibrary.getProtocolManager().updateEntity(entity, observerList);
            }
        }

    }

    @Override
    protected Set<ServerVersion> getSupportedVersions()
    {
        return ServerVersion.NON_188_VERSIONS;
    }
}