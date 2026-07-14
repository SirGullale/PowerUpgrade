package net.power_upgrade;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.power_upgrade.item.Group;
import net.power_upgrade.item.SmithingIngredients;
import net.power_upgrade.item.SmithingTemplates;

import java.util.concurrent.CompletableFuture;

public class PowerUpgradeDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(LangGenerator::new);
	}

	public static class LangGenerator extends FabricLanguageProvider {
		protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, "en_us", registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
			translationBuilder.add(Group.translationKey, "Power Upgrade");

			translationBuilder.add(SmithingIngredients.PowerShard.HINT_TRANSLATION_KEY, "Power Shard");
			translationBuilder.add(SmithingIngredients.PowerShard.POWER_LEVEL_TRANSLATION_KEY, "§f𜻠§r Power Level: %s");
			SmithingIngredients.ENTRIES.forEach(entry -> {
				translationBuilder.add(entry.item().get().getTranslationKey(), entry.translations().itemName());
				translationBuilder.add(entry.appliesToTranslationKey(), entry.translations().appliesTo());
			});
			SmithingTemplates.ENTRIES.forEach(entry -> {
				translationBuilder.add(entry.item().get().getTranslationKey(), entry.translations().itemName());
				translationBuilder.add(entry.upgradeTranslationKey(), entry.translations().upgradeName());
				translationBuilder.add(entry.baseSlotDescriptionTranslationKey(), entry.translations().baseSlotDescription());
				translationBuilder.add(entry.additionsSlotDescriptionTranslationKey(), entry.translations().additionsSlotDescription());
				translationBuilder.add(entry.appliesToTranslationKey(), entry.translations().appliesTo());
				translationBuilder.add(entry.ingredientsTranslationKey(), entry.translations().ingredients());
			});
		}
	}
}
