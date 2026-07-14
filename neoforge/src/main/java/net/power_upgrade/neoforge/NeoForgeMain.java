package net.power_upgrade.neoforge;

import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.power_upgrade.PowerUpgradeMain;
import net.power_upgrade.client.PowerUpgradeClient;

@Mod(PowerUpgradeMain.MOD_ID)
public final class NeoForgeMain {
    public NeoForgeMain(IEventBus modBus) {
        PowerUpgradeMain.config.refresh();
        PowerUpgradeMain.config.save();

        modBus.addListener(RegisterEvent.class, NeoForgeMain::onRegister);
        modBus.addListener(FMLClientSetupEvent.class, event -> PowerUpgradeClient.initClient());
    }

    private static void onRegister(RegisterEvent event) {
        event.register(RegistryKeys.RECIPE_SERIALIZER, helper -> PowerUpgradeMain.registerRecipeSerializers());
        event.register(RegistryKeys.ITEM_GROUP, helper -> PowerUpgradeMain.registerItemGroup());
        event.register(RegistryKeys.ITEM, helper -> PowerUpgradeMain.registerItems());
    }
}
