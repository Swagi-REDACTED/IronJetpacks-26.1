package com.blakebr0.ironjetpacks.mixins.compat;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "techreborn.items.BatteryItem")
public class MixinBatteryItem {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Item.Properties ironjetpacks_injectTrinketComponent(Item.Properties properties) {
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("trinkets")) {
            try {
                properties.component(
                    eu.pb4.trinkets.api.component.TrinketDataComponents.EQUIPMENT,
                    eu.pb4.trinkets.api.component.TrinketEquippable.DEFAULT.withSlots("chest/back").withSwappable(true)
                );
            } catch (Throwable t) {
                // Ignore if component patching fails
            }
        }
        return properties;
    }
}
