package com.blakebr0.ironjetpacks.mixins;

import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {

    @Accessor("pattern")
    net.minecraft.world.item.crafting.ShapedRecipePattern ironjetpacks$getPattern();
    
    @Accessor("result")
    net.minecraft.world.item.ItemStackTemplate ironjetpacks$getResult();
}
