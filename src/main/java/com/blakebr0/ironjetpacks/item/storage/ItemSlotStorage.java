package com.blakebr0.ironjetpacks.item.storage;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ItemSlotStorage extends SingleStackStorage {
    private LivingEntity entity;
    private EquipmentSlot slot;
    
    public ItemSlotStorage(LivingEntity entity, EquipmentSlot slot) {
        this.entity = entity;
        this.slot = slot;
    }
    
    @Override
    public ItemStack getStack() {
        return entity.getItemBySlot(slot);
    }
    
    @Override
    protected void setStack(ItemStack stack) {
        ItemStack currentStack = entity.getItemBySlot(slot);
        
        // Set the stack directly instead of attempting in-place component mutation
        
        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            int slotIndex = -1;
            if (slot == EquipmentSlot.HEAD) slotIndex = 39;
            else if (slot == EquipmentSlot.CHEST) slotIndex = 38;
            else if (slot == EquipmentSlot.LEGS) slotIndex = 37;
            else if (slot == EquipmentSlot.FEET) slotIndex = 36;
            else if (slot == EquipmentSlot.MAINHAND) slotIndex = player.getInventory().getSelectedSlot();
            else if (slot == EquipmentSlot.OFFHAND) slotIndex = 40;
            
            if (slotIndex != -1) {
                player.getInventory().setItem(slotIndex, stack);
                return;
            }
        }
        
        entity.setItemSlot(slot, stack);
    }
}
