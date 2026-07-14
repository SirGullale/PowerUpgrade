package net.power_upgrade.item;

import net.power_upgrade.PowerUpgradeMain;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class Group {
    public static Identifier ID = Identifier.of(PowerUpgradeMain.MOD_ID, "generic");
    public static String translationKey = "itemGroup." + ID.getNamespace() + "." + ID.getPath();
    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID);
    public static ItemGroup GROUP;
    public static Supplier<ItemStack> ICON = () -> new ItemStack(SmithingTemplates.POWER_UPGRADE.item().get());
}
