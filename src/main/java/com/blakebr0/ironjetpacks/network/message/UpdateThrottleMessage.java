package com.blakebr0.ironjetpacks.network.message;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class UpdateThrottleMessage {
    private final int throttleDelta;

    public UpdateThrottleMessage(int throttleDelta) {
        this.throttleDelta = throttleDelta;
    }

    public static UpdateThrottleMessage read(FriendlyByteBuf buffer) {
        return new UpdateThrottleMessage(buffer.readInt());
    }

    public static void write(UpdateThrottleMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.throttleDelta);
    }

    public static void onMessage(UpdateThrottleMessage message, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player != null) {
                ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
                if (chest.getItem() instanceof JetpackItem jetpackItem) {
                    int currentThrottle = jetpackItem.getThrottle(chest);
                    int newThrottle = Math.max(0, Math.min(100, currentThrottle + message.throttleDelta));
                    jetpackItem.setThrottle(chest, newThrottle);
                }
            }
        });
    }
}
