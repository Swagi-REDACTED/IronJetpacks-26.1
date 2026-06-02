package com.blakebr0.ironjetpacks.network.message;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.blakebr0.ironjetpacks.IronJetpacks;
import net.minecraft.network.codec.StreamCodec;

public class ToggleEngineMessage implements CustomPacketPayload {
    public static final Type<ToggleEngineMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "toggle_engine"));
    public static final StreamCodec<FriendlyByteBuf, ToggleEngineMessage> CODEC = CustomPacketPayload.codec(ToggleEngineMessage::write, ToggleEngineMessage::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static ToggleEngineMessage read(FriendlyByteBuf buffer) {
        return new ToggleEngineMessage();
    }

    public static void write(ToggleEngineMessage message, FriendlyByteBuf buffer) {

    }

    public static void onMessage(ToggleEngineMessage message, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player != null) {
                ItemStack stack = com.blakebr0.ironjetpacks.util.JetpackUtils.getEquippedJetpack(player);
                Item item = stack.getItem();
                if (item instanceof JetpackItem jetpack) {
                    jetpack.toggleEngine(stack);
                }
            }
        });
    }
}
