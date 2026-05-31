package com.blakebr0.ironjetpacks.mixins;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(ModelManager.class)
public class MixinModelManager {
    @Inject(method = "loadBlockModels", at = @At("RETURN"))
    private static void ironjetpacks_injectModels(net.minecraft.server.packs.resources.ResourceManager resourceManager, java.util.concurrent.Executor executor, CallbackInfoReturnable<CompletableFuture<Map<Identifier, UnbakedModel>>> cir) {
        cir.setReturnValue(cir.getReturnValue().thenApply(models -> {
            Map<Identifier, UnbakedModel> newModels = new HashMap<>(models);
            
            for (Jetpack pack : JetpackRegistry.getInstance().getAllJetpacks()) {
                String name = pack.name;
                
                inject(newModels, "item/" + name + "_jetpack", """
                    {
                        "parent": "item/generated",
                        "textures": {
                            "layer0": "iron-jetpacks:item/jetpack_strap",
                            "layer1": "iron-jetpacks:item/jetpack"
                        }
                    }
                    """);
                inject(newModels, "item/" + name + "_cell", """
                    {
                        "parent": "item/generated",
                        "textures": {
                            "layer0": "iron-jetpacks:item/cell"
                        }
                    }
                    """);
                inject(newModels, "item/" + name + "_thruster", """
                    {
                        "parent": "item/generated",
                        "textures": {
                            "layer0": "iron-jetpacks:item/thruster"
                        }
                    }
                    """);
                inject(newModels, "item/" + name + "_capacitor", """
                    {
                        "parent": "item/generated",
                        "textures": {
                            "layer0": "iron-jetpacks:item/capacitor"
                        }
                    }
                    """);
            }
            
            return newModels;
        }));
    }
    
    private static void inject(Map<Identifier, UnbakedModel> models, String path, String json) {
        Identifier id = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, path);
        if (!models.containsKey(id)) {
            try {
                models.put(id, UnbakedModelDeserializer.deserialize(new StringReader(json)));
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
