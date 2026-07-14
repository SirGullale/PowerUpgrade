package net.power_upgrade.config;

public class Config {
    public PowerUpgrade power_upgrade = new PowerUpgrade();
    public static class PowerUpgrade { public PowerUpgrade() { }
        public boolean enabled = true;
        public boolean can_upgrade_scaled_items = true;
        public boolean can_upgrade_non_scaled_items = true;
        public int max_power_level = 5;
    }
}
