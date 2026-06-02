package com.blakebr0.ironjetpacks.item;

import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.storage.StackBaseStorage;
import com.blakebr0.ironjetpacks.lib.ModTooltips;
import com.blakebr0.ironjetpacks.mixins.ServerPlayNetworkHandlerAccessor;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import com.blakebr0.ironjetpacks.util.UnitUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.*;
import org.apache.commons.lang3.StringUtils;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class JetpackItem extends Item implements Colored, Enableable {
    private final Jetpack jetpack;
    
    public JetpackItem(Jetpack jetpack, Properties settings) {
        super(settings.humanoidArmor(JetpackUtils.makeArmorMaterial(jetpack), net.minecraft.world.item.equipment.ArmorType.CHESTPLATE).durability(0).rarity(jetpack.rarity));
        this.jetpack = jetpack;
    }
    
    @Override
    public Component getName(ItemStack stack) {
        String name = StringUtils.capitalize(this.jetpack.name.replace(" ", "_"));
        return Component.translatable("item.iron-jetpacks.jetpack", name);
    }
    
    /*
     * Jetpack logic is very much like Simply Jetpacks, since I used it to learn how to make this work
     * Credit to Tonius & Tomson124
     * https://github.com/Tomson124/SimplyJetpacks-2/blob/1.12/src/main/java/tonius/simplyjetpacks/item/rewrite/ItemJetpack.java
     */
    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel worldIn, net.minecraft.world.entity.Entity entityIn, @org.jspecify.annotations.Nullable EquipmentSlot slot) {
        if (!(entityIn instanceof Player player) || slot != EquipmentSlot.CHEST) return;
        tickJetpack(stack, player);
    }
    
    public void tickJetpack(ItemStack stack, Player player) {
        if (!stack.isEmpty() && stack.getItem() instanceof JetpackItem jetpack) {
            if (jetpack.isEngineOn(stack)) {
                boolean hover = jetpack.isHovering(stack);
                if (InputHandler.isHoldingUp(player) || hover && !player.onGround()) {
                    Jetpack info = jetpack.getJetpack();
                    
                    double hoverSpeed = InputHandler.isHoldingDown(player) ? info.speedHover : info.speedHoverSlow;
                    double currentAccel = info.accelVert * (player.getDeltaMovement().y() < 0.3D ? 2.5D : 1.0D);
                    double currentSpeedVertical = info.speedVert * (player.isUnderWater() ? 0.4D : 1.0D);
                    
                    int throttle = getThrottle(stack);
                    double usage = player.isSprinting() ? info.usage * info.sprintFuel : info.usage;
                    usage = usage * (throttle / 100.0);
                    
                    boolean creative = info.creative;
                    
                    EnergyStorage energy = null;
                    if (player.getItemBySlot(EquipmentSlot.CHEST) == stack) {
                        energy = EnergyStorage.ITEM.find(stack, net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.ofSingleSlot(new com.blakebr0.ironjetpacks.item.storage.ItemSlotStorage(player, EquipmentSlot.CHEST)));
                    }
                    if (energy == null && net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("trinkets")) {
                        var access = com.blakebr0.ironjetpacks.compat.trinkets.TrinketsCompat.getEquippedJetpackAccess(player);
                        if (access != null && access.get() == stack) {
                            energy = EnergyStorage.ITEM.find(stack, net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.ofSingleSlot(new com.blakebr0.ironjetpacks.compat.trinkets.TrinketSlotStorage(access)));
                        }
                    }
                    
                    try (Transaction transaction = Transaction.openOuter()) {
                        long extracted = creative ? (long) usage : (energy != null ? energy.extract((long) usage, transaction) : 0);
                        if (creative || extracted > 0) {
                            if (!creative) {
                                transaction.commit();
                            }
    
                            player.fallDistance = 0.0F;
                            if (!player.level().isClientSide()) {
                                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                                    ((com.blakebr0.ironjetpacks.mixins.ServerPlayNetworkHandlerAccessor) serverPlayer.connection).setFloatingTicks(0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    

    
    @Override
    public int getBarWidth(ItemStack stack) {
        EnergyStorage energy = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
        double stored = energy.getCapacity() - energy.getAmount();
        return (int) Math.round(13.0F - (stored / energy.getCapacity()) * 13.0F);
    }
    
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !this.jetpack.creative;
    }
    
    @Environment(EnvType.CLIENT)
    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(final ItemStack stack, final Item.TooltipContext context, final TooltipDisplay display, final java.util.function.Consumer<Component> builder, final TooltipFlag tooltipFlag) {
        if (!this.jetpack.creative) {
            EnergyStorage energy = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
            builder.accept(Component.literal(UnitUtils.formatEnergy(energy.getAmount(), null)).withStyle(ChatFormatting.GRAY).append(" / ").append(Component.literal(UnitUtils.formatEnergy(jetpack.capacity, null))));
        } else {
            builder.accept(Component.literal("-1 E / ").withStyle(ChatFormatting.GRAY).append(ModTooltips.INFINITE.color(ChatFormatting.GRAY)).append(" E"));
        }
        
        Component tier = ModTooltips.TIER.args(this.jetpack.creative ? "Creative" : this.jetpack.tier).withStyle(this.jetpack.rarity.color());
        Component engine = ModTooltips.ENGINE.color(isEngineOn(stack) ? ChatFormatting.GREEN : ChatFormatting.RED);
        Component hover = ModTooltips.HOVER.color(isHovering(stack) ? ChatFormatting.GREEN : ChatFormatting.RED);
        
        builder.accept(ModTooltips.STATE_TOOLTIP_LAYOUT.args(tier, engine, hover));
        
        if (ModConfigs.getClient().general.enableAdvancedInfoTooltips) {
            builder.accept(Component.literal(""));
            if (!com.mojang.blaze3d.platform.InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT) && !com.mojang.blaze3d.platform.InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT)) {
                builder.accept(Component.translatable("tooltip.iron-jetpacks.hold_shift_for_info"));
            } else {
                builder.accept(ModTooltips.FUEL_USAGE.args(this.jetpack.usage + " E/t"));
                builder.accept(ModTooltips.VERTICAL_SPEED.args(this.jetpack.speedVert));
                builder.accept(ModTooltips.VERTICAL_ACCELERATION.args(this.jetpack.accelVert));
                builder.accept(ModTooltips.HORIZONTAL_SPEED.args(this.jetpack.speedSide));
                builder.accept(ModTooltips.HOVER_SPEED.args(this.jetpack.speedHoverSlow));
                builder.accept(ModTooltips.DESCEND_SPEED.args(this.jetpack.speedHover));
                builder.accept(ModTooltips.SPRINT_MODIFIER.args(this.jetpack.sprintSpeed));
                builder.accept(ModTooltips.SPRINT_FUEL_MODIFIER.args(this.jetpack.sprintFuel));
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getColorTint(int i) {
        return i == 1 ? this.jetpack.color : -1;
    }
    

    
    @Override
    public boolean isEnabled() {
        return !this.jetpack.disabled;
    }
    
    public Jetpack getJetpack() {
        return this.jetpack;
    }
    
    // No output
    public double getMaxOutput() {
        return 0;
    }
    
    public double getMaxInput() {
        return jetpack.capacity / 20.0;
    }
    
    public boolean isEngineOn(ItemStack stack) {
        return stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getBoolean("Engine").orElse(false);
    }
    
    public boolean toggleEngine(ItemStack stack) {
        net.minecraft.world.item.component.CustomData data = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
        net.minecraft.nbt.CompoundTag tag = data.copyTag();
        boolean current = tag.getBoolean("Engine").orElse(false);
        tag.putBoolean("Engine", !current);
        net.minecraft.world.item.component.CustomData.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, tag);
        return !current;
    }
    
    public int getThrottle(ItemStack stack) {
        net.minecraft.world.item.component.CustomData data = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
        return data.copyTag().getInt("Throttle").orElse(100);
    }
    
    public void setThrottle(ItemStack stack, int throttle) {
        net.minecraft.world.item.component.CustomData data = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
        net.minecraft.nbt.CompoundTag tag = data.copyTag();
        tag.putInt("Throttle", throttle);
        net.minecraft.world.item.component.CustomData.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, tag);
    }
    
    public boolean isHovering(ItemStack stack) {
        return stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getBoolean("Hover").orElse(false);
    }
    
    public boolean toggleHover(ItemStack stack) {
        net.minecraft.world.item.component.CustomData data = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
        net.minecraft.nbt.CompoundTag tag = data.copyTag();
        boolean current = tag.getBoolean("Hover").orElse(false);
        tag.putBoolean("Hover", !current);
        net.minecraft.world.item.component.CustomData.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, tag);
        return !current;
    }
    
    private void fly(Player player, double y) {
        net.minecraft.world.phys.Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x(), y, motion.z());
    }
}
