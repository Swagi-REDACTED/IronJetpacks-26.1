package com.blakebr0.ironjetpacks.mixins;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    /**
     * Suppress detectEquipmentUpdates from treating energy-only changes on jetpack
     * items as equipment changes. Without this, every tick of fuel consumption
     * triggers equipment update packets, attribute re-evaluation, and can cause
     * cascading sound/animation glitches on both server and client.
     */
    @Inject(method = "equipmentHasChanged", at = @At("HEAD"), cancellable = true)
    private void ironjetpacks_equipmentHasChanged(ItemStack previous, ItemStack current,
            CallbackInfoReturnable<Boolean> cir) {
        if (!previous.isEmpty() && !current.isEmpty()
                && previous.getItem() instanceof JetpackItem && current.getItem() == previous.getItem()) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Suppress the equip sound that plays when a jetpack's energy component changes.
     * This is triggered via ArmorSlot.setByPlayer -> onEquipItem when the creative
     * inventory's CreativeInventoryListener sends slot updates back to the server
     * (ServerboundSetCreativeModeSlotPacket), causing a feedback loop of:
     * energy drain -> broadcastChanges -> client listener -> setByPlayer -> onEquipItem -> sound.
     *
     * We cancel onEquipItem when both old and new stacks are the same JetpackItem,
     * since the only difference is the energy component.
     */
    @Inject(method = "onEquipItem", at = @At("HEAD"), cancellable = true)
    private void ironjetpacks_onEquipItem(EquipmentSlot slot, ItemStack oldStack, ItemStack stack,
            CallbackInfo ci) {
        if (!oldStack.isEmpty() && !stack.isEmpty()
                && oldStack.getItem() instanceof JetpackItem && stack.getItem() == oldStack.getItem()) {
            ci.cancel();
        }
    }
}
