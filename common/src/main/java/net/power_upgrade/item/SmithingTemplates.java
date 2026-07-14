package net.power_upgrade.item;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.power_upgrade.PowerUpgradeMain;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SmithingTemplates {

    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;
    private static final Identifier EMPTY_ARMOR_SLOT_HELMET_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_helmet");
    private static final Identifier EMPTY_ARMOR_SLOT_SWORD_TEXTURE = Identifier.ofVanilla("item/empty_slot_sword");
    private static final Identifier EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_chestplate");
    private static final Identifier EMPTY_ARMOR_SLOT_PICKAXE_TEXTURE = Identifier.ofVanilla("item/empty_slot_pickaxe");
    private static final Identifier EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_leggings");
    private static final Identifier EMPTY_ARMOR_SLOT_AXE_TEXTURE = Identifier.ofVanilla("item/empty_slot_axe");
    private static final Identifier EMPTY_ARMOR_SLOT_BOOTS_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_boots");
    private static final Identifier EMPTY_ARMOR_SLOT_HOE_TEXTURE = Identifier.ofVanilla("item/empty_slot_hoe");
    private static final Identifier EMPTY_ARMOR_SLOT_SHOVEL_TEXTURE = Identifier.ofVanilla("item/empty_slot_shovel");

    private static final Identifier EMPTY_SLOT_SMALL_POWER_SHARD = PowerUpgradeMain.identifierOf("item/empty_slot_small_power_shard");
    private static final Identifier EMPTY_SLOT_MEDIUM_POWER_SHARD = PowerUpgradeMain.identifierOf("item/empty_slot_medium_power_shard");
    private static final Identifier EMPTY_SLOT_BIG_POWER_SHARD = PowerUpgradeMain.identifierOf("item/empty_slot_big_power_shard");
    private static final Identifier EMPTY_SLOT_HUGE_POWER_SHARD = PowerUpgradeMain.identifierOf("item/empty_slot_huge_power_shard");
    private static final Identifier EMPTY_SLOT_MAGNIFICENT_POWER_SHARD = PowerUpgradeMain.identifierOf("item/empty_slot_magnificent_power_shard");

    public record Translations(String itemName, String upgradeName, String appliesTo, String ingredients, String baseSlotDescription, String additionsSlotDescription) { }
    public record Entry(String name, Translations translations, Supplier<SmithingTemplateItem> item) {
        public static Entry of(String name, Translations translations) {
            var entry = new Entry(name, translations, null);
            var factory = Suppliers.memoize(() -> new SmithingTemplateItem(
                    entry.appliesToText(),
                    entry.ingredientsText(),
                    entry.upgradeText(),
                    entry.baseSlotDescriptionText(),
                    entry.additionsSlotDescriptionText(),
                    baseSlotTextures(),
                    additionsTextures(), new FeatureFlag[0]
            ));
            return new Entry(name, translations, factory);
        }

        public Identifier id() {
            return Identifier.of(PowerUpgradeMain.MOD_ID, name + "_smithing_template");
        }

        public String upgradeTranslationKey() {
            return Util.createTranslationKey("upgrade", Identifier.of(PowerUpgradeMain.MOD_ID, name + "_smithing_template"));
        }
        public Text upgradeText() {
            return Text.translatable(upgradeTranslationKey()).formatted(TITLE_FORMATTING);
        }

        public String appliesToTranslationKey() {
            return Util.createTranslationKey("item", Identifier.of(PowerUpgradeMain.MOD_ID, name + "_smithing_template.applies_to"));
        }
        public Text appliesToText() {
            return Text.translatable(appliesToTranslationKey()).formatted(DESCRIPTION_FORMATTING);
        }

        public String ingredientsTranslationKey() {
            return Util.createTranslationKey("item", Identifier.of(PowerUpgradeMain.MOD_ID, name + "_smithing_template.ingredients"));
        }
        public Text ingredientsText() {
            var key = ingredientsTranslationKey();
            return Text.translatable(key).formatted(DESCRIPTION_FORMATTING);
        }

        public String baseSlotDescriptionTranslationKey() {
            return Util.createTranslationKey("item", Identifier.of(PowerUpgradeMain.MOD_ID, name + "_smithing_template.base_slot_description"));
        }
        public Text baseSlotDescriptionText() {
            return Text.translatable(baseSlotDescriptionTranslationKey());
        }

        public String additionsSlotDescriptionTranslationKey() {
            return Util.createTranslationKey("item", Identifier.of(PowerUpgradeMain.MOD_ID, name + "_smithing_template.additions_slot_description"));
        }
        public Text additionsSlotDescriptionText() {
            return Text.translatable(additionsSlotDescriptionTranslationKey());
        }

        private static List<Identifier> baseSlotTextures() {
            return List.of(EMPTY_ARMOR_SLOT_HELMET_TEXTURE,
                    EMPTY_ARMOR_SLOT_SWORD_TEXTURE,
                    EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE,
                    EMPTY_ARMOR_SLOT_PICKAXE_TEXTURE,
                    EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE,
                    EMPTY_ARMOR_SLOT_AXE_TEXTURE,
                    EMPTY_ARMOR_SLOT_BOOTS_TEXTURE,
                    EMPTY_ARMOR_SLOT_HOE_TEXTURE,
                    EMPTY_ARMOR_SLOT_SHOVEL_TEXTURE);
        }
        private static List<Identifier> additionsTextures() {
            return List.of(EMPTY_SLOT_SMALL_POWER_SHARD,
                    EMPTY_SLOT_MEDIUM_POWER_SHARD,
                    EMPTY_SLOT_BIG_POWER_SHARD,
                    EMPTY_SLOT_HUGE_POWER_SHARD,
                    EMPTY_SLOT_MAGNIFICENT_POWER_SHARD);
        }
    }

    public static final ArrayList<Entry> ENTRIES = new ArrayList<>();
    public static Entry add(Entry entry) {
        ENTRIES.add(entry);
        return entry;
    }

    public static final Entry POWER_UPGRADE = add(Entry.of("power_upgrade",
            new Translations(
                    "Smithing Template",
                    "Power Upgrade",
                    "Equipment",
                    "Power Shard",
                    "Add a piece of armor, weapon or tool",
                    "Add power upgrade"
            ))
    );

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