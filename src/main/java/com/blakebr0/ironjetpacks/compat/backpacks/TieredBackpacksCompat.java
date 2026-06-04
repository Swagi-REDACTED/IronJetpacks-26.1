package com.blakebr0.ironjetpacks.compat.backpacks;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import team.reborn.energy.api.EnergyStorage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;

public class TieredBackpacksCompat {
    public static void tryChargeFromBackpack(ItemStack jetpack, EnergyStorage jetpackEnergy, Player player, ItemStack backpackStack) {
        if (!BuiltInRegistries.ITEM.getKey(backpackStack.getItem()).getPath().contains("backpack")) {
            return;
        }

        ItemContainerContents contents = backpackStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if (contents == ItemContainerContents.EMPTY) return;

        int capacity = 27;
        String id = BuiltInRegistries.ITEM.getKey(backpackStack.getItem()).toString();
        if (id.contains("copper")) capacity = 36;
        else if (id.contains("iron")) capacity = 45;
        else if (id.contains("golden")) capacity = 54;
        else if (id.contains("diamond")) capacity = 63;
        else if (id.contains("netherite")) capacity = 72;

        net.minecraft.core.NonNullList<ItemStack> items = net.minecraft.core.NonNullList.withSize(capacity, ItemStack.EMPTY);
        contents.copyInto(items);

        boolean changed = false;
        long jetpackCapacity = 0;
        if (jetpackEnergy != null) {
            jetpackCapacity = jetpackEnergy.getCapacity() - jetpackEnergy.getAmount();
            if (jetpackCapacity <= 0) return;
        } else {
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            ItemStack innerStack = items.get(i);
            if (innerStack.isEmpty() || innerStack == jetpack) continue;

            EnergyStorage batteryEnergy = EnergyStorage.ITEM.find(innerStack, ContainerItemContext.withConstant(innerStack));
            if (batteryEnergy != null && batteryEnergy.getAmount() > 0 && batteryEnergy.supportsExtraction()) {
                final int index = i;
                net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage storage = new net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage() {
                    @Override
                    protected ItemStack getStack() {
                        return items.get(index);
                    }
                    @Override
                    protected void setStack(ItemStack stack) {
                        items.set(index, stack);
                    }
                };
                
                EnergyStorage activeBattery = EnergyStorage.ITEM.find(innerStack, ContainerItemContext.ofSingleSlot(storage));
                if (activeBattery != null) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        long extracted = activeBattery.extract(jetpackCapacity, transaction);
                        if (extracted > 0) {
                            long inserted = jetpackEnergy.insert(extracted, transaction);
                            if (inserted != extracted) {
                                transaction.abort();
                                try (Transaction tx2 = Transaction.openOuter()) {
                                    long ex2 = activeBattery.extract(inserted, tx2);
                                    jetpackEnergy.insert(ex2, tx2);
                                    tx2.commit();
                                }
                            } else {
                                transaction.commit();
                            }
                            changed = true;
                            jetpackCapacity -= inserted;
                            if (jetpackCapacity <= 0) break;
                        }
                    }
                }
            }
        }

        if (changed) {
            backpackStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        }
    }
}

