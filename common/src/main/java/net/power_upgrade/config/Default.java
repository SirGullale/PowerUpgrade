package net.power_upgrade.config;

public class Default {

    public static Config config = createDefaultConfig();

    private static Config createDefaultConfig() {
        var config = new Config();
        config.power_upgrade.enabled = true;
        config.power_upgrade.can_upgrade_scaled_items = false;
        config.power_upgrade.can_upgrade_non_scaled_items = true;
        config.power_upgrade.max_power_level = 5;
        return config;
    }
}
