package com.blakebr0.ironjetpacks.util;

import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.item.storage.ItemSlotStorage;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import team.reborn.energy.api.EnergyStorage;

public class JetpackUtils {
    public static ItemStack getEquippedJetpack(Player player) {
        ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (stack.isEmpty() || !(stack.getItem() instanceof JetpackItem)) {
            if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("trinkets")) {
                var access = com.blakebr0.ironjetpacks.compat.trinkets.TrinketsCompat.getEquippedJetpackAccess(player);
                if (access != null) {
                    stack = access.get();
                }
            }
        }
        return stack;
    }

    public static boolean isFlying(Player player) {
        ItemStack stack = getEquippedJetpack(player);
        boolean isTrinket = stack != player.getItemBySlot(EquipmentSlot.CHEST);

        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof JetpackItem jetpack) {
                long amount = 0;
                if (!isTrinket) {
                    ItemSlotStorage storage = new ItemSlotStorage(player, EquipmentSlot.CHEST);
                    amount = EnergyStorage.ITEM.find(stack, ContainerItemContext.ofSingleSlot(storage)).getAmount();
                } else {
                    var access = com.blakebr0.ironjetpacks.compat.trinkets.TrinketsCompat.getEquippedJetpackAccess(player);
                    if (access != null) {
                        amount = EnergyStorage.ITEM.find(stack, ContainerItemContext.ofSingleSlot(new com.blakebr0.ironjetpacks.compat.trinkets.TrinketSlotStorage(access))).getAmount();
                    }
                }

                if (jetpack.isEngineOn(stack) && (amount > 0 || player.isCreative() || jetpack.getJetpack().creative)) {
                    if (jetpack.isHovering(stack)) {
                        return !player.onGround();
                    } else {
                        return InputHandler.isHoldingUp(player);
                    }
                }
            }
        }
        
        return false;
    }
    
    public static ArmorMaterial makeArmorMaterial(Jetpack jetpack) {
        return new ArmorMaterial(
            0,
            java.util.Map.of(ArmorType.CHESTPLATE, jetpack.armorPoints),
            Math.max(1, jetpack.enchantablilty),
            SoundEvents.ARMOR_EQUIP_GENERIC,
            0.0F,
            0.0F,
            ItemTags.REPAIRS_IRON_ARMOR,
            ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("iron-jetpacks", "jetpack_" + jetpack.name))
        );
    }
}
