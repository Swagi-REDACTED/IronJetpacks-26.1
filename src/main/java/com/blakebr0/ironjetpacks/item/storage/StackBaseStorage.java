package com.blakebr0.ironjetpacks.item.storage;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;

public class StackBaseStorage extends SingleStackStorage {
    private ItemStack stack;
    
    public StackBaseStorage(ItemStack stack) {
        this.stack = stack;
    }
    
    @Override
    protected ItemStack getStack() {
        return stack;
    }
    
    @Override
    protected void setStack(ItemStack stack) {
        this.stack = stack;
    }
}
