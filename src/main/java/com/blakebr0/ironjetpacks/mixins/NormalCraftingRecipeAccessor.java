package com.blakebr0.ironjetpacks.mixins;

import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NormalCraftingRecipe.class)
public interface NormalCraftingRecipeAccessor {

    @Accessor("commonInfo")
    net.minecraft.world.item.crafting.Recipe.CommonInfo getCommonInfo();
    
    @Accessor("bookInfo")
    net.minecraft.world.item.crafting.CraftingRecipe.CraftingBookInfo getBookInfo();
}
