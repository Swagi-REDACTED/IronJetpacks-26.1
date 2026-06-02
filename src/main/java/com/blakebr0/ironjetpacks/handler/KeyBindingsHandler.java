package com.blakebr0.ironjetpacks.handler;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.lib.ModTooltips;
import com.blakebr0.ironjetpacks.network.NetworkHandler;
import com.blakebr0.ironjetpacks.network.message.ToggleEngineMessage;
import com.blakebr0.ironjetpacks.network.message.ToggleHoverMessage;
import com.blakebr0.ironjetpacks.network.message.UpdateInputMessage;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeyBindingsHandler {
    public static final net.minecraft.client.KeyMapping.Category CATEGORY = net.minecraft.client.KeyMapping.Category.register(net.minecraft.resources.Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "iron_jetpacks"));
    public static KeyMapping keyEngine;
    public static KeyMapping keyHover;
    public static KeyMapping keyDescend;
    public static KeyMapping keyThrottle;
    
    private static boolean up = false;
    private static boolean down = false;
    private static boolean forwards = false;
    private static boolean backwards = false;
    private static boolean left = false;
    private static boolean right = false;
    
    public static void onClientSetup() {
        keyEngine = create("engine", GLFW.GLFW_KEY_V, IronJetpacks.NAME);
        keyHover = create("hover", GLFW.GLFW_KEY_G, IronJetpacks.NAME);
        keyDescend = create("descend", GLFW.GLFW_KEY_LEFT_CONTROL, IronJetpacks.NAME);
        keyThrottle = create("throttle", GLFW.GLFW_KEY_R, IronJetpacks.NAME);
    }
    
    private static KeyMapping create(String id, int key, String category) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping("key." + IronJetpacks.MOD_ID + "." + id, InputConstants.Type.KEYSYM, key, CATEGORY));
    }
    
    public static void onClientTick(Minecraft client) {
        handleInputs(client);
        updateInputs(client);
    }
    
    private static void handleInputs(Minecraft client) {
        Player player = client.player;
        if (player == null)
            return;
        
        ItemStack chest = com.blakebr0.ironjetpacks.util.JetpackUtils.getEquippedJetpack(player);
        Item item = chest.getItem();
        
        if (item instanceof JetpackItem) {
            JetpackItem jetpack = (JetpackItem) item;
            
            while (keyEngine.consumeClick()) {
                NetworkHandler.sendToServer(new ToggleEngineMessage());
                boolean on = !jetpack.isEngineOn(chest);
                Component state = on ? ModTooltips.ON.color(ChatFormatting.GREEN) : ModTooltips.OFF.color(ChatFormatting.RED);
                player.sendOverlayMessage(ModTooltips.TOGGLE_ENGINE.args(state));
            }
            
            while (keyHover.consumeClick()) {
                NetworkHandler.sendToServer(new ToggleHoverMessage());
                boolean on = !jetpack.isHovering(chest);
                Component state = on ? ModTooltips.ON.color(ChatFormatting.GREEN) : ModTooltips.OFF.color(ChatFormatting.RED);
                player.sendOverlayMessage(ModTooltips.TOGGLE_HOVER.args(state));
            }
        }
    }
    
    /*
     * Keyboard handling borrowed from Simply Jetpacks
     * https://github.com/Tomson124/SimplyJetpacks-2/blob/1.12/src/main/java/tonius/simplyjetpacks/client/handler/KeyTracker.java
     */
    public static void updateInputs(Minecraft client) {
        Options settings = client.options;
        
        if (client.getConnection() == null)
            return;
        
        boolean upNow = settings.keyJump.isDown();
        boolean downNow = keyDescend.isUnbound() ? settings.keyShift.isDown() : keyDescend.isDown();
        boolean forwardsNow = settings.keyUp.isDown();
        boolean backwardsNow = settings.keyDown.isDown();
        boolean leftNow = settings.keyLeft.isDown();
        boolean rightNow = settings.keyRight.isDown();
        
        if (upNow != up || downNow != down || forwardsNow != forwards || backwardsNow != backwards || leftNow != left || rightNow != right) {
            up = upNow;
            down = downNow;
            forwards = forwardsNow;
            backwards = backwardsNow;
            left = leftNow;
            right = rightNow;
            
            NetworkHandler.sendToServer(new UpdateInputMessage(upNow, downNow, forwardsNow, backwardsNow, leftNow, rightNow));
            InputHandler.update(client.player, upNow, downNow, forwardsNow, backwardsNow, leftNow, rightNow);
        }
    }
}
