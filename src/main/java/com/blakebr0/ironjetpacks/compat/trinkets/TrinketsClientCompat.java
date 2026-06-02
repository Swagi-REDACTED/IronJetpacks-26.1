package com.blakebr0.ironjetpacks.compat.trinkets;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.client.model.JetpackModel;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.mojang.blaze3d.vertex.PoseStack;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.client.TrinketRenderer;
import eu.pb4.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class TrinketsClientCompat {
    public static void registerRenderer(Jetpack jetpack) {
        TrinketRendererRegistry.registerRenderer(jetpack.item.get(), new TrinketRenderer() {
            private JetpackModel model;

            @Override
            public void submit(ItemStack stack, TrinketSlotAccess slotReference, EntityModel<? extends LivingEntityRenderState> contextModel, PoseStack poseStack, SubmitNodeCollector submit, int light, LivingEntityRenderState state, float limbAngle, float limbDistance) {
                if (!(contextModel instanceof HumanoidModel<?> humanoidModel) || !(state instanceof HumanoidRenderState humanoidState)) return;
                
                int colorTint = jetpack.item.get().getColorTint(1) | 0xFF000000;
                
                JetpackModel delegateModel = getModel();
                TrinketRenderer.followBodyRotations(contextModel, delegateModel);
                
                poseStack.pushPose();
                TrinketRenderer.translateToChest(poseStack, humanoidModel, humanoidState);
                
                // Revert Trinkets offset so standard ArmorModel aligns correctly to the torso
                poseStack.translate(0.0F, -0.4F, 0.16F);
                
                delegateModel.setupAnim(humanoidState);
                
                net.minecraft.client.renderer.OrderedSubmitNodeCollector collector = (net.minecraft.client.renderer.OrderedSubmitNodeCollector) submit;
                
                // Base layer with color tint
                collector.submitModel(
                    delegateModel, humanoidState, poseStack,
                    net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "textures/armor/jetpack.png")),
                    light, OverlayTexture.NO_OVERLAY, colorTint, null, 0, null
                );
                
                // Overlay layer without tint
                collector.submitModel(
                    delegateModel, humanoidState, poseStack,
                    net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "textures/armor/jetpack_overlay.png")),
                    light, OverlayTexture.NO_OVERLAY, -1, null, 0, null
                );
                
                poseStack.popPose();
            }

            private JetpackModel getModel() {
                if (model == null) {
                    model = new JetpackModel(jetpack.item.get());
                }
                return model;
            }
        });
    }
}
