package com.blakebr0.ironjetpacks.compat.trinkets;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class TrinketSlotStorage extends SingleStackStorage {
    private final TrinketSlotAccess slotAccess;

    public TrinketSlotStorage(TrinketSlotAccess slotAccess) {
        this.slotAccess = slotAccess;
    }

    @Override
    public ItemStack getStack() {
        return slotAccess.get();
    }

    @Override
    protected void setStack(ItemStack stack) {
        ItemStack currentStack = slotAccess.get();
        if (!currentStack.isEmpty() && !stack.isEmpty() && currentStack.getItem() == stack.getItem()) {
            if (!Objects.equals(currentStack.getComponentsPatch(), stack.getComponentsPatch())) {
                for (DataComponentType<?> type : currentStack.getComponents().keySet()) {
                    currentStack.set(type, null);
                }
                currentStack.applyComponents(stack.getComponents());
            }
            currentStack.setCount(stack.getCount());
            return;
        }
        slotAccess.set(stack);
    }
}
