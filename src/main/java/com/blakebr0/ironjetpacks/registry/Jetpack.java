package com.blakebr0.ironjetpacks.registry;

import com.blakebr0.ironjetpacks.item.ComponentItem;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.google.common.base.Suppliers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import com.blakebr0.ironjetpacks.IronJetpacks;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class Jetpack {
    public String name;
    public int tier;
    public int color;
    public int armorPoints;
    public int enchantablilty;
    public String craftingMaterialString;
    private Ingredient craftingMaterial;
    public Supplier<JetpackItem> item;
    public boolean creative = false;
    public boolean disabled = false;
    public Rarity rarity = Rarity.COMMON;
    public ComponentItem cell;
    public ComponentItem thruster;
    public ComponentItem capacitor;
    public double capacity;
    public double usage;
    public double speedVert;
    public double accelVert;
    public double speedSide;
    public double speedHover;
    public double speedHoverSlow;
    public double sprintSpeed;
    public double sprintFuel;
    
    public Jetpack(String name, int tier, int color, int armorPoints, int enchantability, String craftingMaterialString) {
        this.name = name;
        this.tier = tier;
        this.color = color;
        this.armorPoints = armorPoints;
        this.enchantablilty = enchantability;
        this.craftingMaterialString = craftingMaterialString;
        this.item = Suppliers.memoize(() -> new JetpackItem(this, new Item.Properties().setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, this.name + "_jetpack")))));
    }
    
    public Jetpack setStats(double capacity, double usage, double speedVert, double accelVert, double speedSide, double speedHover, double speedHoverSlow, double sprintSpeed, double sprintFuel) {
        this.capacity = capacity;
        this.usage = usage;
        this.speedVert = speedVert;
        this.accelVert = accelVert;
        this.speedSide = speedSide;
        this.speedHover = speedHover;
        this.speedHoverSlow = speedHoverSlow;
        this.sprintSpeed = sprintSpeed;
        this.sprintFuel = sprintFuel;
        
        return this;
    }
    
    public Jetpack setCreative() {
        this.creative = true;
        this.tier = -1;
        this.rarity = Rarity.EPIC;
        
        return this;
    }
    
    public Jetpack setCreative(boolean set) {
        if (set) this.setCreative();
        return this;
    }
    
    public Jetpack setDisabled() {
        this.disabled = true;
        return this;
    }
    
    public Jetpack setDisabled(boolean set) {
        if (set) this.setDisabled();
        return this;
    }
    
    public Jetpack setRarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }
    
    public Jetpack setCellItem(ComponentItem item) {
        this.cell = item;
        return this;
    }
    
    public Jetpack setThrusterItem(ComponentItem item) {
        this.thruster = item;
        return this;
    }
    
    public Jetpack setCapacitorItem(ComponentItem item) {
        this.capacitor = item;
        return this;
    }
    
    public int getTier() {
        return this.tier;
    }
    
    public Ingredient getCraftingMaterial(net.minecraft.core.HolderLookup.Provider registries) {
        if (this.craftingMaterial == null) {
            System.out.println("[IronJetpacks] DEBUG: getCraftingMaterial called for '" + this.name + "' with craftingMaterialString='" + this.craftingMaterialString + "'");
            try {
                if (!this.craftingMaterialString.equalsIgnoreCase("null")) {
                    String[] parts = craftingMaterialString.split(":");
                    if (parts.length >= 3 && this.craftingMaterialString.startsWith("tag:")) {
                        net.minecraft.tags.TagKey<Item> tag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath(parts[1], parts[2]));
                        if (tag != null) {
                            java.util.Optional<net.minecraft.core.HolderSet.Named<Item>> tagSet;
                            if (registries != null) {
                                tagSet = registries.lookupOrThrow(net.minecraft.core.registries.Registries.ITEM).get(tag);
                            } else {
                                tagSet = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(tag);
                            }
                            if (tagSet.isPresent()) {
                                this.craftingMaterial = net.minecraft.world.item.crafting.Ingredient.of(tagSet.get());
                            } else {
                                // Tag not found - fall back to direct item lookup
                                String resolvedItem = null;
                                Item fallback = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("minecraft", this.name + "_ingot"));
                                if (fallback != null && fallback != net.minecraft.world.item.Items.AIR) {
                                    resolvedItem = "minecraft:" + this.name + "_ingot";
                                } else {
                                    fallback = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("techreborn", this.name + "_ingot"));
                                    if (fallback != null && fallback != net.minecraft.world.item.Items.AIR) {
                                        resolvedItem = "techreborn:" + this.name + "_ingot";
                                    }
                                }
                                if (resolvedItem != null) {
                                    System.out.println("[IronJetpacks] Tag fallback: using " + resolvedItem + " for material: " + this.name);
                                    this.craftingMaterial = Ingredient.of(fallback);
                                    this.craftingMaterialString = resolvedItem;
                                    // Permanently fix the JSON config
                                    try {
                                        java.io.File configDir = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("iron-jetpacks/jetpacks").toFile();
                                        java.io.File file = new java.io.File(configDir, this.name + ".json");
                                        if (file.exists()) {
                                            com.google.gson.Gson gson = com.blakebr0.ironjetpacks.config.json.Serializers.initGson();
                                            java.io.FileWriter writer = new java.io.FileWriter(file);
                                            writer.write(gson.toJson(this));
                                            writer.close();
                                            System.out.println("[IronJetpacks] Fixed config: " + file.getName() + " -> " + resolvedItem);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    System.out.println("[IronJetpacks] WARNING: No item found for material: " + this.name);
                                }
                            }
                        }
                    } else if (parts.length >= 2) {
                        Item item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(parts[0], parts[1]));
                        if (item != null)
                            this.craftingMaterial = Ingredient.of(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return this.craftingMaterial;
    }
}
