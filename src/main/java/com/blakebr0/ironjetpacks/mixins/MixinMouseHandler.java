package com.blakebr0.ironjetpacks.mixins;

import com.blakebr0.ironjetpacks.handler.KeyBindingsHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.network.NetworkHandler;
import com.blakebr0.ironjetpacks.network.message.UpdateThrottleMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void ironjetpacks_onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null && window == mc.getWindow().handle()) {
            if (KeyBindingsHandler.keyThrottle != null && KeyBindingsHandler.keyThrottle.isDown()) {
                ItemStack chest = com.blakebr0.ironjetpacks.util.JetpackUtils.getEquippedJetpack(mc.player);
                if (!chest.isEmpty() && chest.getItem() instanceof JetpackItem jetpackItem) {
                    int step = com.blakebr0.ironjetpacks.config.ModConfigs.getClient().general.throttleStepAmount.get();
                    int delta = (vertical > 0 ? 1 : (vertical < 0 ? -1 : 0)) * step;
                    if (delta != 0) {
                        int currentThrottle = jetpackItem.getThrottle(chest);
                        int newThrottle = Math.max(0, Math.min(100, currentThrottle + delta));
                        if (currentThrottle != newThrottle) {
                            jetpackItem.setThrottle(chest, newThrottle);
                            NetworkHandler.sendToServer(new UpdateThrottleMessage(delta));
                        }
                    }
                    ci.cancel();
                }
            }
        }
    }
}
