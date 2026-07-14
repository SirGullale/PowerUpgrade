package net.power_upgrade.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.dungeon_difficulty.logic.ItemScaling;
import net.power_upgrade.PowerUpgradeMain;
import net.power_upgrade.item.SmithingIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.SmithingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SmithingScreenHandler.class)
public class SmithingScreenHandlerMixin {

    @WrapOperation(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/SmithingRecipe;craft(Lnet/minecraft/recipe/input/RecipeInput;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/item/ItemStack;")
    )
    private static ItemStack onCraft(
            SmithingRecipe instance,
            RecipeInput recipeInput,
            RegistryWrapper.WrapperLookup wrapperLookup,
            Operation<ItemStack> original) {

        var crafted = original.call(instance, recipeInput, wrapperLookup);
        var input = (SmithingRecipeInput) recipeInput;
        var base = input.base();
        var template = input.template();
        var addition = input.addition();

        if (template.isEmpty()) return crafted;

        var templateKey = template.getRegistryEntry().getKey();

        if (templateKey.isEmpty()) return crafted;
        if (!templateKey.get().getValue().toString().contains("upgrade")) return crafted;
        if (base.isEmpty() || base.isOf(Items.AIR)) return crafted;

        int powerLevel = 0;
        if(addition.getItem() instanceof SmithingIngredients.PowerShard powerShard) powerLevel = powerShard.getPowerLevel();

        var config = PowerUpgradeMain.config.value;
        var powerUpgrade = config.power_upgrade;

        if (ItemScaling.isScaled(base)) {
            if (!powerUpgrade.can_upgrade_scaled_items) return ItemStack.EMPTY;
            double currentLevel = ItemScaling.getScaleFactor(base);
            if (currentLevel + powerLevel > powerUpgrade.max_power_level) return ItemStack.EMPTY;
            ItemScaling.rescale(crafted, (int) currentLevel + powerLevel);
        } else {
            if (powerUpgrade.can_upgrade_non_scaled_items) ItemScaling.rescale(crafted, powerLevel);
            else return ItemStack.EMPTY;
        }

        return crafted;
    }
}
