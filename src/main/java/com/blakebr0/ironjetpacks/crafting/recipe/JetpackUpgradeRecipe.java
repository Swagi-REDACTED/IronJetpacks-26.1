package com.blakebr0.ironjetpacks.crafting.recipe;

import com.blakebr0.ironjetpacks.crafting.ModRecipeSerializers;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.mixins.ShapedRecipeAccessor;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;

public class JetpackUpgradeRecipe extends ShapedRecipe {
    
    public JetpackUpgradeRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo, pattern, result);
    }
    
    @Override
    public ItemStack assemble(CraftingInput inv) {
        ItemStack jetpack = inv.getItem(4);
        ItemStack result = ((ShapedRecipeAccessor) this).getResult().create();
        
        if (!jetpack.isEmpty() && jetpack.getItem() instanceof JetpackItem) {
            result.applyComponents(jetpack.getComponentsPatch());
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public RecipeSerializer<ShapedRecipe> getSerializer() {
        return (RecipeSerializer<ShapedRecipe>) (Object) ModRecipeSerializers.CRAFTING_JETPACK_UPGRADE.get();
    }
    
    public static final MapCodec<JetpackUpgradeRecipe> MAP_CODEC = ShapedRecipe.MAP_CODEC.xmap(
            r -> new JetpackUpgradeRecipe(((com.blakebr0.ironjetpacks.mixins.NormalCraftingRecipeAccessor) r).getCommonInfo(), ((com.blakebr0.ironjetpacks.mixins.NormalCraftingRecipeAccessor) r).getBookInfo(),
                    ((ShapedRecipeAccessor) r).getPattern(), ((ShapedRecipeAccessor) r).getResult()),
            r -> r);

    public static final StreamCodec<RegistryFriendlyByteBuf, JetpackUpgradeRecipe> STREAM_CODEC = ShapedRecipe.STREAM_CODEC.map(
            r -> new JetpackUpgradeRecipe(((com.blakebr0.ironjetpacks.mixins.NormalCraftingRecipeAccessor) r).getCommonInfo(),
                    ((com.blakebr0.ironjetpacks.mixins.NormalCraftingRecipeAccessor) r).getBookInfo(), ((ShapedRecipeAccessor) r).getPattern(),
                    ((ShapedRecipeAccessor) r).getResult()),
            r -> r);
}
