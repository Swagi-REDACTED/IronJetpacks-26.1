package com.blakebr0.ironjetpacks.handler;

import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.sound.JetpackSound;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class JetpackClientHandler {
    private static final RandomSource RANDOM = RandomSource.create();
    
    public static void onClientTick(Minecraft client) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null) {
            if (!mc.isPaused()) {
                ItemStack chest = com.blakebr0.ironjetpacks.util.JetpackUtils.getEquippedJetpack(mc.player);
                Item item = chest.getItem();
                if (!chest.isEmpty() && item instanceof JetpackItem && JetpackUtils.isFlying(mc.player)) {
                    Jetpack jetpack = ((JetpackItem) item).getJetpack();
                    
                    int throttle = ((JetpackItem) item).getThrottle(chest);
                    double throttleScale = throttle / 100.0;
                    
                    boolean hover = ((JetpackItem) item).isHovering(chest);
                    double hoverSpeed = InputHandler.isHoldingDown(mc.player) ? jetpack.speedHover : jetpack.speedHoverSlow;
                    double currentAccel = (jetpack.accelVert * throttleScale) * (mc.player.getDeltaMovement().y() < 0.3D ? 2.5D : 1.0D);
                    double currentSpeedVertical = (jetpack.speedVert * throttleScale) * (mc.player.isUnderWater() ? 0.4D : 1.0D);
                    double speedSideScaled = jetpack.speedSide * throttleScale;
                    
                    double motionY = mc.player.getDeltaMovement().y();
                    if (InputHandler.isHoldingUp(mc.player)) {
                        if (!hover) {
                            if (motionY < currentSpeedVertical) {
                                fly(mc.player, Math.min(motionY + currentAccel, currentSpeedVertical));
                            }
                        } else {
                            if (InputHandler.isHoldingDown(mc.player)) {
                                if (motionY < -jetpack.speedHoverSlow) {
                                    fly(mc.player, Math.min(motionY + currentAccel, -jetpack.speedHoverSlow));
                                }
                            } else {
                                if (motionY < jetpack.speedHover) {
                                    fly(mc.player, Math.min(motionY + currentAccel, jetpack.speedHover));
                                }
                            }
                        }
                    } else {
                        if (motionY < -hoverSpeed) {
                            fly(mc.player, Math.min(motionY + currentAccel, -hoverSpeed));
                        }
                    }
                    
                    float speedSideways = (float) (mc.player.isShiftKeyDown() ? speedSideScaled * 0.5F : speedSideScaled);
                    float speedForward = (float) (mc.player.isSprinting() ? speedSideways * jetpack.sprintSpeed : speedSideways);
                    
                    if (InputHandler.isHoldingForwards(mc.player)) {
                        mc.player.moveRelative(1, new net.minecraft.world.phys.Vec3(0, 0, speedForward));
                    }
                    if (InputHandler.isHoldingBackwards(mc.player)) {
                        mc.player.moveRelative(1, new net.minecraft.world.phys.Vec3(0, 0, -speedSideways * 0.8F));
                    }
                    if (InputHandler.isHoldingLeft(mc.player)) {
                        mc.player.moveRelative(1, new net.minecraft.world.phys.Vec3(speedSideways, 0, 0));
                    }
                    if (InputHandler.isHoldingRight(mc.player)) {
                        mc.player.moveRelative(1, new net.minecraft.world.phys.Vec3(-speedSideways, 0, 0));
                    }
                    
                    if (ModConfigs.getClient().general.enableJetpackParticles && (mc.options.particles().get() != ParticleStatus.MINIMAL)) {
                        Vec3 playerPos = mc.player.position().add(0, 1.5, 0);
                        
                        float random = (RANDOM.nextFloat() - 0.5F) * 0.1F;
                        double[] sneakBonus = mc.player.isShiftKeyDown() ? new double[]{-0.30, -0.10} : new double[]{0, 0};
                        
                        Vec3 rotation = Vec3.directionFromRotation(0, mc.player.yBodyRot);
                        Vec3 vLeft = new Vec3(-0.18, -0.90 + sneakBonus[1], -0.30 + sneakBonus[0]).xRot(0).yRot(mc.player.yBodyRot * -0.017453292F);
                        Vec3 vRight = new Vec3(0.18, -0.90 + sneakBonus[1], -0.30 + sneakBonus[0]).xRot(0).yRot(mc.player.yBodyRot * -0.017453292F);
                        
                        Vec3 v = playerPos.add(vLeft).add(mc.player.getDeltaMovement().scale(jetpack.speedSide));
                        mc.particleEngine.createParticle(ParticleTypes.FLAME, v.x, v.y, v.z, random, -0.2D, random);
                        mc.particleEngine.createParticle(ParticleTypes.SMOKE, v.x, v.y, v.z, random, -0.2D, random);
                        
                        v = playerPos.add(vRight).add(mc.player.getDeltaMovement().scale(jetpack.speedSide));
                        mc.particleEngine.createParticle(ParticleTypes.FLAME, v.x, v.y, v.z, random, -0.2D, random);
                        mc.particleEngine.createParticle(ParticleTypes.SMOKE, v.x, v.y, v.z, random, -0.2D, random);
                    }
                    
                    if (ModConfigs.getClient().general.enableJetpackSounds && !jetpack.creative && !JetpackSound.playing(mc.player.getId())) {
                        mc.getSoundManager().play(new JetpackSound(mc.player, RANDOM));
                    }
                }
            }
        }
    }
    
    private static void fly(net.minecraft.world.entity.player.Player player, double y) {
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x(), y, motion.z());
    }
}
