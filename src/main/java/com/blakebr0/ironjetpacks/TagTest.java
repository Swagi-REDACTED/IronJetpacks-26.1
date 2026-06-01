package com.blakebr0.ironjetpacks;
public class TagTest {
    public static void checkTags(net.minecraft.core.HolderLookup.Provider registries) {
        net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ingots/copper"));
        boolean present = registries.lookupOrThrow(net.minecraft.core.registries.Registries.ITEM).get(tag).isPresent();
        System.out.println("TAG C:INGOTS/COPPER PRESENT: " + present);
    }
}
