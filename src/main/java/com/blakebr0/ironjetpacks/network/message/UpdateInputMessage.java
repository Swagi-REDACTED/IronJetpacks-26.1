package com.blakebr0.ironjetpacks.network.message;

import com.blakebr0.ironjetpacks.handler.InputHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.blakebr0.ironjetpacks.IronJetpacks;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

public class UpdateInputMessage implements CustomPacketPayload {
    public static final Type<UpdateInputMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "update_input"));
    public static final StreamCodec<FriendlyByteBuf, UpdateInputMessage> CODEC = CustomPacketPayload.codec(UpdateInputMessage::write, UpdateInputMessage::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    private final boolean up;
    private final boolean down;
    private final boolean forwards;
    private final boolean backwards;
    private final boolean left;
    private final boolean right;

    public UpdateInputMessage(boolean up, boolean down, boolean forwards, boolean backwards, boolean left,
            boolean right) {
        this.up = up;
        this.down = down;
        this.forwards = forwards;
        this.backwards = backwards;
        this.left = left;
        this.right = right;
    }

    public static UpdateInputMessage read(FriendlyByteBuf buffer) {
        return new UpdateInputMessage(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(),
                buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    public static void write(UpdateInputMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.up);
        buffer.writeBoolean(message.down);
        buffer.writeBoolean(message.forwards);
        buffer.writeBoolean(message.backwards);
        buffer.writeBoolean(message.left);
        buffer.writeBoolean(message.right);
    }

    public static void onMessage(UpdateInputMessage message, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player != null) {
                InputHandler.update(player, message.up, message.down, message.forwards, message.backwards, message.left,
                        message.right);
            }
        });
    }
}
