package com.blakebr0.ironjetpacks.network;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.network.message.ToggleEngineMessage;
import com.blakebr0.ironjetpacks.network.message.ToggleHoverMessage;
import com.blakebr0.ironjetpacks.network.message.UpdateInputMessage;
import com.blakebr0.ironjetpacks.network.message.UpdateThrottleMessage;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class NetworkHandler {
    public static final CustomPacketPayload.Type<WrapperPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, IronJetpacks.MOD_ID));

    public record WrapperPayload(FriendlyByteBuf buf) implements CustomPacketPayload {
        public static final StreamCodec<RegistryFriendlyByteBuf, WrapperPayload> CODEC = StreamCodec.of(
            (buffer, payload) -> {
                int readable = payload.buf().readableBytes();
                buffer.writeBytes(payload.buf(), payload.buf().readerIndex(), readable);
            },
            (buffer) -> {
                int readable = buffer.readableBytes();
                FriendlyByteBuf copy = new FriendlyByteBuf(Unpooled.buffer(readable));
                copy.writeBytes(buffer, buffer.readerIndex(), readable);
                buffer.skipBytes(readable);
                return new WrapperPayload(copy);
            }
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }
    }
    
    public static void onCommonSetup() {
        PayloadTypeRegistry.serverboundPlay().register(PACKET_TYPE, WrapperPayload.CODEC);
        
        ServerPlayNetworking.registerGlobalReceiver(PACKET_TYPE, (payload, context) -> {
            FriendlyByteBuf buf = payload.buf();
            int id = buf.readInt();
            switch (id) {
                case 0 -> ToggleHoverMessage.onMessage(ToggleHoverMessage.read(buf), context.server(), context.player());
                case 1 -> UpdateInputMessage.onMessage(UpdateInputMessage.read(buf), context.server(), context.player());
                case 2 -> ToggleEngineMessage.onMessage(ToggleEngineMessage.read(buf), context.server(), context.player());
                case 3 -> UpdateThrottleMessage.onMessage(UpdateThrottleMessage.read(buf), context.server(), context.player());
            }
        });
    }
    
    @Environment(EnvType.CLIENT)
    public static void sendToServer(ToggleHoverMessage message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(0);
        ToggleHoverMessage.write(message, buf);
        ClientPlayNetworking.send(new WrapperPayload(buf));
    }
    
    @Environment(EnvType.CLIENT)
    public static void sendToServer(UpdateInputMessage message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(1);
        UpdateInputMessage.write(message, buf);
        ClientPlayNetworking.send(new WrapperPayload(buf));
    }
    
    @Environment(EnvType.CLIENT)
    public static void sendToServer(ToggleEngineMessage message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(2);
        ToggleEngineMessage.write(message, buf);
        ClientPlayNetworking.send(new WrapperPayload(buf));
    }
    
    @Environment(EnvType.CLIENT)
    public static void sendToServer(UpdateThrottleMessage message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(3);
        UpdateThrottleMessage.write(message, buf);
        ClientPlayNetworking.send(new WrapperPayload(buf));
    }
}
