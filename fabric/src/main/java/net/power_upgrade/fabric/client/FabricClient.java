package net.power_upgrade.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.power_upgrade.client.PowerUpgradeClient;

public final class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PowerUpgradeClient.initClient();
    }
}
