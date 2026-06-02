package com.blakebr0.ironjetpacks.compat.trinkets;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import net.minecraft.world.item.Item;

public class TrinketsCompat {
    public static void init() {
        for (com.blakebr0.ironjetpacks.registry.Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
            Item item = jetpack.item.get();
            TrinketCallback.setCallback(item, new TrinketCallback() {
                @Override
                public void tick(ItemStack stack, TrinketSlotAccess slot, LivingEntity entity) {
                    if (entity instanceof Player player) {
                        if (item instanceof JetpackItem jetpackItem) {
                            jetpackItem.tickJetpack(stack, player);
                        }
                    }
                }

                @Override
                public boolean canEquipFromUse(ItemStack stack, LivingEntity entity) {
                    return true;
                }
            });
        }
    }

    public static boolean isJetpackEquipped(Player player) {
        var component = TrinketsApi.getAttachment(player);
        if (component != null) {
            var equipped = component.getEquipped(stack -> stack.getItem() instanceof JetpackItem);
            return !equipped.isEmpty();
        }
        return false;
    }

    public static TrinketSlotAccess getEquippedJetpackAccess(Player player) {
        var component = TrinketsApi.getAttachment(player);
        if (component != null) {
            var equipped = component.getEquipped(stack -> stack.getItem() instanceof JetpackItem);
            if (!equipped.isEmpty()) {
                var ref = equipped.get(0).getA();
                return ref.inventory().getSlotAccess(ref.index());
            }
        }
        return null;
    }

    public static void applyTrinketComponent(Item.Properties properties) {
        properties.component(eu.pb4.trinkets.api.component.TrinketDataComponents.EQUIPMENT, eu.pb4.trinkets.api.component.TrinketEquippable.DEFAULT.withSlots("chest/back").withSwappable(true));
    }
}
