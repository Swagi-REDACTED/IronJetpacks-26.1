package com.blakebr0.ironjetpacks.client;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.client.model.JetpackModel;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.handler.HudHandler;
import com.blakebr0.ironjetpacks.handler.JetpackClientHandler;
import com.blakebr0.ironjetpacks.handler.KeyBindingsHandler;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class IronJetpacksClient {
    public static void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(KeyBindingsHandler::onClientTick);
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "hud"), HudHandler::onRenderGameOverlay);
        ClientTickEvents.END_CLIENT_TICK.register(JetpackClientHandler::onClientTick);
        
        KeyBindingsHandler.onClientSetup();
        ModelHandler.onClientSetup();
        
        ModConfigs.getClient();
        for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
            ArmorRenderer.register(new ArmorRenderer() {
                private JetpackModel model;
                
                @Override
                public void render(PoseStack matrices, SubmitNodeCollector vertexConsumers, ItemStack stack, HumanoidRenderState entity, EquipmentSlot slot, int light, HumanoidModel<HumanoidRenderState> contextModel) {
                    int colorTint = jetpack.item.get().getColorTint(1) | 0xFF000000;
                    
                    JetpackModel delegateModel = getModel();
                    delegateModel.setupAnim(entity);
                    
                    net.minecraft.client.renderer.OrderedSubmitNodeCollector collector = (net.minecraft.client.renderer.OrderedSubmitNodeCollector) vertexConsumers;
                    
                    // Base layer with color tint (jetpack.png)
                    collector.submitModel(
                        delegateModel, entity, matrices,
                        net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "textures/armor/jetpack.png")),
                        light, OverlayTexture.NO_OVERLAY, colorTint, null, 0, null
                    );
                    
                    // Overlay layer without tint (jetpack_overlay.png)
                    collector.submitModel(
                        delegateModel, entity, matrices,
                        net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "textures/armor/jetpack_overlay.png")),
                        light, OverlayTexture.NO_OVERLAY, -1, null, 0, null
                    );
                }
                
                private JetpackModel getModel() {
                    if (model == null) {
                        model = new JetpackModel(jetpack.item.get());
                    }
                    
                    return model;
                }
            }, jetpack.item.get());
            
            if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("trinkets")) {
                com.blakebr0.ironjetpacks.compat.trinkets.TrinketsClientCompat.registerRenderer(jetpack);
            }
        }
    }
}
