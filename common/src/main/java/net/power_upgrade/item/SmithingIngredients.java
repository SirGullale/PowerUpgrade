package net.power_upgrade.item;

import com.google.common.base.Suppliers;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.power_upgrade.PowerUpgradeMain;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SmithingIngredients {

    public static class PowerShard extends Item {
        public static final Text APPLIES_TO_TEXT = Text.translatable(
                        Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.applies_to")))
                .formatted(Formatting.GRAY);
        public static final String HINT_TRANSLATION_KEY = Util.createTranslationKey("item", Identifier.of(PowerUpgradeMain.MOD_ID, "smithing_template.hint"));
        public static final Text HINT_TEXT = Text.translatable(HINT_TRANSLATION_KEY)
                .formatted(Formatting.GRAY);
        public static final String POWER_LEVEL_TRANSLATION_KEY = Util.createTranslationKey("item", Identifier.of(PowerUpgradeMain.MOD_ID, "power_level"));

        private final int powerLevel;
        private final String appliesToTranslationKey;

        public PowerShard(Item.Settings settings, int powerLevel, String appliesToTranslationKey) {
            super(settings);
            this.powerLevel = powerLevel;
            this.appliesToTranslationKey = appliesToTranslationKey;
        }

        public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(HINT_TEXT);
            tooltip.add(Text.translatable(POWER_LEVEL_TRANSLATION_KEY, powerLevel).formatted(Formatting.BLUE));
            tooltip.add(ScreenTexts.EMPTY);
            tooltip.add(APPLIES_TO_TEXT);
            tooltip.add(ScreenTexts.space().append(Text.translatable(appliesToTranslationKey)).formatted(Formatting.BLUE));
        }

        public int getPowerLevel() {
            return powerLevel;
        }
    }

    public record Translations(String itemName, String appliesTo) { }

    public record Entry(String name, Translations translations, Supplier<PowerShard> item) {
        public static Entry of(String name, int powerLevel, Translations translations) {
            var entry = new SmithingIngredients.Entry(name, translations, null);
            var factory = Suppliers.memoize(() ->
                    new PowerShard(new Item.Settings()
                            .rarity(Rarity.EPIC)
                            .fireproof(),
                            powerLevel,
                            entry.appliesToTranslationKey()
                    ));
            return new Entry(name, translations, factory);
        }
        public Identifier id() {
            return Identifier.of(PowerUpgradeMain.MOD_ID, name);
        }

        public static String appliesToTranslationKey(String name) {
            return Util.createTranslationKey("item", Identifier.of(PowerUpgradeMain.MOD_ID,name + ".applies_to"));
        }
        public String appliesToTranslationKey() {
            return appliesToTranslationKey(name);
        }
    }

    public static final ArrayList<Entry> ENTRIES = new ArrayList<>();
    public static Entry add(Entry entry) {
        ENTRIES.add(entry);
        return entry;
    }

    public static final Entry SMALL_POWER_SHARD = add(Entry.of("small_power_shard",1, new Translations("Small Power Shard","Equipment")
    ));
    public static final Entry MEDIUM_POWER_SHARD = add(Entry.of("medium_power_shard",2, new Translations("Medium Power Shard","Equipment")
    ));
    public static final Entry BIG_POWER_SHARD = add(Entry.of("big_power_shard",3, new Translations("Big Power Shard","Equipment")
    ));
    public static final Entry HUGE_POWER_SHARD = add(Entry.of("huge_power_shard",4, new Translations("Huge Power Shard","Equipment")
    ));
    public static final Entry MAGNIFICENT_POWER_SHARD = add(Entry.of("magnificent_power_shard", 5, new Translations("Magnificent Power Shard","Equipment")
    ));
    public static void register() {
        for (var entry : ENTRIES) {
            Registry.register(Registries.ITEM, entry.id(), entry.item().get());
        }
        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register((content) -> {
            for (var entry : ENTRIES) {
                content.add(entry.item().get());
            }
        });
    }
}
