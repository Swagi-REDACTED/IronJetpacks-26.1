package com.blakebr0.ironjetpacks.config;

import com.blakebr0.ironjetpacks.IronJetpacks;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;

public class ModConfigs {

    public static class Client extends Config {
        public General general = new General();
        public Hud hud = new Hud();

        public Client() {
            super(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "client"));
        }

        public static class General extends ConfigSection {
            public boolean enableJetpackSounds = true;
            public boolean enableJetpackParticles = true;
            public boolean enableAdvancedInfoTooltips = true;
            public me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt throttleStepAmount = new me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt(1, 5, 1);
        }

        public static class Hud extends ConfigSection {
            public boolean enableHud = true;
            public ValidatedInt hudPosition = new ValidatedInt(1, 5, 0); // 0=Top Left, 1=Middle Left, 2=Bottom Left, 3=Top Right, 4=Middle Right, 5=Bottom Right
            public int hudOffsetX = 0;
            public int hudOffsetY = 0;
            public boolean showHudOverChat = false;
            public boolean advancedDisplay = true;
        }
    }
    
    public static class Common extends Config {
        public General general = new General();
        public Recipe recipe = new Recipe();

        public Common() {
            super(Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "common"));
        }

        public static class General extends ConfigSection {
            public boolean enchantableJetpacks = false;
        }
        
        public static class Recipe extends ConfigSection {
            public boolean enableCellRecipes = true;
            public boolean enableThrusterRecipes = true;
            public boolean enableCapacitorRecipes = true;
            public boolean enableJetpackRecipes = true;
        }
    }
    
    private static Common COMMON_INSTANCE = null;
    private static Client CLIENT_INSTANCE = null;

    public static Common get() {
        if (COMMON_INSTANCE == null) {
            COMMON_INSTANCE = ConfigApiJava.registerAndLoadConfig(Common::new);
        }
        return COMMON_INSTANCE;
    }
    
    @Environment(EnvType.CLIENT)
    public static Client getClient() {
        if (CLIENT_INSTANCE == null) {
            CLIENT_INSTANCE = ConfigApiJava.registerAndLoadConfig(Client::new);
        }
        return CLIENT_INSTANCE;
    }
}
