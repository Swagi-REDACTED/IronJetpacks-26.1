package com.blakebr0.ironjetpacks.mixins;

import com.blakebr0.ironjetpacks.crafting.JetpackDynamicRecipeManager;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {
    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeMap;create(Ljava/lang/Iterable;)Lnet/minecraft/world/item/crafting/RecipeMap;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void postPrepare(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<net.minecraft.world.item.crafting.RecipeMap> cir, java.util.SortedMap<Identifier, Recipe<?>> recipes, java.util.List<net.minecraft.world.item.crafting.RecipeHolder<?>> recipeHolders) {
        net.minecraft.core.HolderLookup.Provider registries = ((com.blakebr0.ironjetpacks.mixins.RecipeManagerAccessor) this).ironjetpacks$getRegistries();
        JetpackDynamicRecipeManager.appendRecipes(registries, (id, recipe) -> {
            net.minecraft.resources.ResourceKey<Recipe<?>> key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id);
            recipeHolders.add(new net.minecraft.world.item.crafting.RecipeHolder<>(key, recipe));
        });
    }
}
