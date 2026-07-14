package net.power_upgrade.fabric;

import net.fabricmc.api.ModInitializer;
import net.power_upgrade.PowerUpgradeMain;

public class FabricMain implements ModInitializer {

	@Override
	public void onInitialize() {
		PowerUpgradeMain.config.refresh();
		PowerUpgradeMain.registerRecipeSerializers();
		PowerUpgradeMain.registerItemGroup();
		PowerUpgradeMain.registerItems();
		PowerUpgradeMain.config.save();
	}
}