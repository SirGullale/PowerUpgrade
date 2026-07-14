package net.power_upgrade;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.power_upgrade.config.Config;
import net.power_upgrade.config.Default;
import net.power_upgrade.item.Group;
import net.power_upgrade.item.PowerUpgradeSmithingRecipe;
import net.power_upgrade.item.SmithingTemplates;
import net.power_upgrade.item.SmithingIngredients;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tiny_config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerUpgradeMain {

	public static final String MOD_ID = "power_upgrade";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ConfigManager<Config> config = new ConfigManager<>
			("power_upgrade", Default.config)
			.builder()
			.setDirectory(MOD_ID)
			.sanitize(true)
			.build();
	public static final RecipeSerializer<PowerUpgradeSmithingRecipe> POWER_SMITHING_SERIALIZER =
			new PowerUpgradeSmithingRecipe.Serializer();

	public static void registerRecipeSerializers() {
		Registry.register(
				Registries.RECIPE_SERIALIZER,
				Identifier.of(MOD_ID, "power_smithing"),
				POWER_SMITHING_SERIALIZER
		);
	}

	public static void registerItemGroup() {
		Group.GROUP = FabricItemGroup.builder()
				.icon(Group.ICON)
				.displayName(Text.translatable(Group.translationKey))
				.build();
		Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.GROUP);
	}

	public static void registerItems() {
		SmithingTemplates.register();
		SmithingIngredients.register();
	}

	public static Identifier identifierOf(String name) {
		return Identifier.of(MOD_ID, name);
	}
}