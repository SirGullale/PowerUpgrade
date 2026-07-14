package net.power_upgrade.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dungeon_difficulty.logic.ItemScaling;
import net.power_upgrade.PowerUpgradeMain;
import net.minecraft.item.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public record PowerUpgradeSmithingRecipe(Ingredient template, Ingredient addition) implements SmithingRecipe {

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        return this.template.test(input.template())
                && !input.base().isEmpty()
                && isPowerUpgradable(input.base())
                && this.addition.test(input.addition());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return input.base().copy();
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup lookup) {
        return ItemStack.EMPTY;
        //return new ItemStack(SmithingIngredients.SMALL_POWER_SHARD.item().get());
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return template.test(stack);
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return !stack.isEmpty() && isPowerUpgradable(stack);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return SmithingIngredients.ENTRIES.stream()
                .anyMatch(e -> stack.isOf(e.item().get()));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PowerUpgradeMain.POWER_SMITHING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    private boolean isPowerUpgradable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (ItemScaling.isScaled(stack)) {
            double currentLevel = ItemScaling.getScaleFactor(stack);

            var config = PowerUpgradeMain.config.value;
            var powerUpgrade = config.power_upgrade;
            return currentLevel <= powerUpgrade.max_power_level;
        } else {
            var item = stack.getItem();
            return item instanceof ToolItem
                    || item instanceof RangedWeaponItem
                    || item instanceof ArmorItem
                    || item instanceof ShieldItem;
        }
    }

    // -------------------------------------------------------------------------
    // Serializer
    // -------------------------------------------------------------------------
    public static class Serializer implements RecipeSerializer<PowerUpgradeSmithingRecipe> {

        private static final MapCodec<PowerUpgradeSmithingRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("template").forGetter(r -> r.template),
                        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(r -> r.addition)
                ).apply(instance, PowerUpgradeSmithingRecipe::new));

        private static final PacketCodec<RegistryByteBuf, PowerUpgradeSmithingRecipe> PACKET_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, r -> r.template,
                        Ingredient.PACKET_CODEC, r -> r.addition,
                        PowerUpgradeSmithingRecipe::new
                );

        @Override
        public MapCodec<PowerUpgradeSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PowerUpgradeSmithingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
