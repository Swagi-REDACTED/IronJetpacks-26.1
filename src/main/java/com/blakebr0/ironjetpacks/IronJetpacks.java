package com.blakebr0.ironjetpacks;

import com.blakebr0.ironjetpacks.compat.ftl.FtlCompat;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.crafting.ModRecipeSerializers;
import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.item.ModItems;
import com.blakebr0.ironjetpacks.network.NetworkHandler;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.blakebr0.ironjetpacks.sound.ModSounds;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class IronJetpacks implements ModInitializer {
    public static final String MOD_ID = "iron-jetpacks";
    public static final String NAME = "Iron Jetpacks";
    
    public static final CreativeModeTab ITEM_GROUP = FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup.iron-jetpacks.iron-jetpacks"))
            .icon(() -> {
                return new ItemStack(ModItems.STRAP.get());
            })
            .displayItems((featureFlagSet, output) -> {
                for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
                    output.accept(jetpack.cell);
                    output.accept(jetpack.thruster);
                    output.accept(jetpack.capacitor);
                    JetpackItem item = jetpack.item.get();
                    output.accept(new ItemStack(item));
                    
                    if (!jetpack.creative) {
                        ItemStack stack = new ItemStack(item);
                        team.reborn.energy.api.base.SimpleEnergyItem.setStoredEnergyUnchecked(stack, (long) jetpack.capacity);
                        output.accept(stack);
                    }
                }
            })
            .build();
    
    @Override
    public void onInitialize() {
        ModItems.register();
        ModSounds.register();
        ModRecipeSerializers.register();
        ModRecipeSerializers.onCommonSetup();
        
        net.minecraft.core.Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(MOD_ID, MOD_ID), ITEM_GROUP);
        
        NetworkHandler.onCommonSetup();
        
        ModConfigs.get();
        
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            try {
                Class.forName("com.blakebr0.ironjetpacks.client.IronJetpacksClient").getDeclaredMethod("onInitializeClient").invoke(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        FtlCompat.init();
        
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> InputHandler.clear());
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> InputHandler.onChangeDimension(player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> InputHandler.onLogout(handler.getPlayer()));
    }
}
