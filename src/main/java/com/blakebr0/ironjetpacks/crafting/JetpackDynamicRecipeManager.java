package com.blakebr0.ironjetpacks.crafting;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.crafting.recipe.JetpackUpgradeRecipe;
import com.blakebr0.ironjetpacks.item.ModItems;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class JetpackDynamicRecipeManager {
    public static void appendRecipes(net.minecraft.core.HolderLookup.Provider registries, BiConsumer<Identifier, Recipe<?>> appender) {
        JetpackRegistry.getInstance().getAllJetpacks().forEach(jetpack -> {
            makeCellRecipe(jetpack, registries, appender);
            makeThrusterRecipe(jetpack, registries, appender);
            makeCapacitorRecipe(jetpack, registries, appender);
            makeJetpackRecipe(jetpack, registries, appender);
            makeJetpackUpgradeRecipe(jetpack, registries, appender);
        });
    }
    
    private static void makeCellRecipe(Jetpack jetpack, net.minecraft.core.HolderLookup.Provider registries, BiConsumer<Identifier, Recipe<?>> appender) {
        if (!ModConfigs.get().recipe.enableCellRecipes)
            return;
        
        JetpackRegistry jetpacks = JetpackRegistry.getInstance();
        
        Ingredient material = jetpack.getCraftingMaterial(registries);
        Item coilItem = jetpacks.getCoilForTier(jetpack.tier);
        if (material == null || coilItem == null)
            return;
        
        Ingredient coil = Ingredient.of(coilItem);
        Ingredient redstone = Ingredient.of(Items.REDSTONE);
        net.minecraft.world.item.crafting.ShapedRecipePattern pattern = net.minecraft.world.item.crafting.ShapedRecipePattern.of(Map.of(
                'M', material,
                'C', coil,
                'R', redstone
        ), " R ", "MCM", " R ");
        
        Identifier name = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_cell");
        ShapedRecipe recipe = new ShapedRecipe(new Recipe.CommonInfo(true), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""), pattern, new ItemStackTemplate(jetpack.cell));
        appender.accept(name, recipe);
    }
    
    private static void makeThrusterRecipe(Jetpack jetpack, net.minecraft.core.HolderLookup.Provider registries, BiConsumer<Identifier, Recipe<?>> appender) {
        if (!ModConfigs.get().recipe.enableThrusterRecipes)
            return;
        
        JetpackRegistry jetpacks = JetpackRegistry.getInstance();
        
        Ingredient material = jetpack.getCraftingMaterial(registries);
        Item coilItem = jetpacks.getCoilForTier(jetpack.tier);
        if (material == null || coilItem == null)
            return;
        
        Ingredient coil = Ingredient.of(coilItem);
        Ingredient cell = Ingredient.of(jetpack.cell);
        Ingredient furnace = Ingredient.of(Blocks.FURNACE);
        net.minecraft.world.item.crafting.ShapedRecipePattern pattern = net.minecraft.world.item.crafting.ShapedRecipePattern.of(Map.of(
                'M', material,
                'C', coil,
                'E', cell,
                'F', furnace
        ), "MCM", "CEC", "MFM");
        
        Identifier name = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_thruster");
        ShapedRecipe recipe = new ShapedRecipe(new Recipe.CommonInfo(true), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""), pattern, new ItemStackTemplate(jetpack.thruster));
        appender.accept(name, recipe);
    }
    
    private static void makeCapacitorRecipe(Jetpack jetpack, net.minecraft.core.HolderLookup.Provider registries, BiConsumer<Identifier, Recipe<?>> appender) {
        if (!ModConfigs.get().recipe.enableCapacitorRecipes)
            return;
        
        Ingredient material = jetpack.getCraftingMaterial(registries);
        if (material == null)
            return;
        
        Ingredient cell = Ingredient.of(jetpack.cell);
        net.minecraft.world.item.crafting.ShapedRecipePattern pattern = net.minecraft.world.item.crafting.ShapedRecipePattern.of(Map.of(
                'M', material,
                'C', cell
        ), "MCM", "MCM", "MCM");
        
        Identifier name = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_capacitor");
        ShapedRecipe recipe = new ShapedRecipe(new Recipe.CommonInfo(true), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""), pattern, new ItemStackTemplate(jetpack.capacitor));
        appender.accept(name, recipe);
    }
    
    private static void makeJetpackRecipe(Jetpack jetpack, net.minecraft.core.HolderLookup.Provider registries, BiConsumer<Identifier, Recipe<?>> appender) {
        if (!ModConfigs.get().recipe.enableJetpackRecipes)
            return;
        
        JetpackRegistry jetpacks = JetpackRegistry.getInstance();
        if (jetpack.tier != jetpacks.getLowestTier())
            return;
        
        Ingredient material = jetpack.getCraftingMaterial(registries);
        if (material == null)
            return;
        
        Ingredient capacitor = Ingredient.of(jetpack.capacitor);
        Ingredient thruster = Ingredient.of(jetpack.thruster);
        Ingredient strap = Ingredient.of(ModItems.STRAP.get());
        net.minecraft.world.item.crafting.ShapedRecipePattern pattern = net.minecraft.world.item.crafting.ShapedRecipePattern.of(Map.of(
                'M', material,
                'C', capacitor,
                'S', strap,
                'T', thruster
        ), "MCM", "MSM", "T T");
        
        Identifier name = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_jetpack");
        ShapedRecipe recipe = new ShapedRecipe(new Recipe.CommonInfo(true), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""), pattern, new ItemStackTemplate(jetpack.item.get()));
        appender.accept(name, recipe);
    }
    
    private static void makeJetpackUpgradeRecipe(Jetpack jetpack, net.minecraft.core.HolderLookup.Provider registries, BiConsumer<Identifier, Recipe<?>> appender) {
        if (!ModConfigs.get().recipe.enableJetpackRecipes)
            return;
        
        JetpackRegistry jetpacks = JetpackRegistry.getInstance();
        if (jetpack.tier == jetpacks.getLowestTier())
            return;
        
        Ingredient material = jetpack.getCraftingMaterial(registries);
        if (material == null)
            return;
        
        Ingredient capacitor = Ingredient.of(jetpack.capacitor);
        Ingredient thruster = Ingredient.of(jetpack.thruster);
        Ingredient jetpackTier = Ingredient.of(ModRecipeSerializers.ALL_JETPACKS.stream()
                .filter(item -> item.getJetpack().tier == jetpack.tier - 1)
                .toArray(ItemLike[]::new));
        net.minecraft.world.item.crafting.ShapedRecipePattern pattern = net.minecraft.world.item.crafting.ShapedRecipePattern.of(Map.of(
                'M', material,
                'C', capacitor,
                'J', jetpackTier,
                'T', thruster
        ), "MCM", "MJM", "T T");
        
        Identifier name = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_jetpack");
        ShapedRecipe recipe = new ShapedRecipe(new Recipe.CommonInfo(true), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""), pattern, new ItemStackTemplate(jetpack.item.get()));
        appender.accept(name, recipe);
    }
}
