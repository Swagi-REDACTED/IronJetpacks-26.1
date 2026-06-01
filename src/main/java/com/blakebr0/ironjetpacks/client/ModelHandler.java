package com.blakebr0.ironjetpacks.client;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModelHandler {
    private static final Logger LOGGER = LogManager.getLogger(IronJetpacks.NAME);

    /** Stores item type ("jetpack", "cell", etc.) and tint color for each dynamic model. */
    private record ModelInfo(String type, int color) {}

    private static final Map<Identifier, ModelInfo> DYNAMIC_MODELS = new HashMap<>();

    public static void onClientSetup() {
        JetpackRegistry.getInstance().getAllJetpacks().forEach(pack -> {
            Identifier cellLocation = BuiltInRegistries.ITEM.getKey(pack.cell);
            if (cellLocation != null) {
                DYNAMIC_MODELS.put(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/" + cellLocation.getPath()), new ModelInfo("cell", pack.color));
            }

            Identifier capacitorLocation = BuiltInRegistries.ITEM.getKey(pack.capacitor);
            if (capacitorLocation != null) {
                DYNAMIC_MODELS.put(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/" + capacitorLocation.getPath()), new ModelInfo("capacitor", pack.color));
            }

            Identifier thrusterLocation = BuiltInRegistries.ITEM.getKey(pack.thruster);
            if (thrusterLocation != null) {
                DYNAMIC_MODELS.put(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/" + thrusterLocation.getPath()), new ModelInfo("thruster", pack.color));
            }

            Identifier jetpackLocation = BuiltInRegistries.ITEM.getKey(pack.item.get());
            if (jetpackLocation != null) {
                DYNAMIC_MODELS.put(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "item/" + jetpackLocation.getPath()), new ModelInfo("jetpack", pack.color));
            }
        });

        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.modifyItemModelBeforeBake().register((model, context) -> {
                Identifier id = context.itemId();
                Identifier modelKey = Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
                ModelInfo info = DYNAMIC_MODELS.get(modelKey);
                if (info != null) {
                    // Build the correct tint sources from the jetpack color:
                    // - Jetpacks have 2 layers: layer0 (strap, white/no-tint) + layer1 (body, colored)
                    // - Components have 1 layer: layer0 (colored)
                    List<ItemTintSource> tints;
                    if (info.type().equals("jetpack")) {
                        tints = List.of(
                            new Constant(0xFFFFFF),   // layer0: strap (white = no tint)
                            new Constant(info.color()) // layer1: jetpack body color
                        );
                    } else {
                        tints = List.of(
                            new Constant(info.color()) // layer0: component color
                        );
                    }
                    return new net.minecraft.client.renderer.item.CuboidItemModelWrapper.Unbaked(modelKey, java.util.Optional.empty(), tints);
                }
                return model;
            });

            pluginContext.modifyModelOnLoad().register(ModelModifier.OVERRIDE_PHASE, (model, context) -> {
                Identifier id = context.id();
                ModelInfo info = DYNAMIC_MODELS.get(id);
                if (info != null) {
                    String json;
                    if (info.type().equals("jetpack")) {
                        json = """
                        {
                            "parent": "item/generated",
                            "textures": {
                                "layer0": "iron-jetpacks:item/jetpack_strap",
                                "layer1": "iron-jetpacks:item/jetpack"
                            }
                        }
                        """;
                    } else {
                        json = """
                        {
                            "parent": "item/generated",
                            "textures": {
                                "layer0": "iron-jetpacks:item/%s"
                            }
                        }
                        """.formatted(info.type());
                    }
                    try {
                        return UnbakedModelDeserializer.deserialize(new StringReader(json));
                    } catch (Exception e) {
                        LOGGER.error("Failed to generate dynamic model for {}", id, e);
                    }
                }
                return model;
            });
        });
    }
}
