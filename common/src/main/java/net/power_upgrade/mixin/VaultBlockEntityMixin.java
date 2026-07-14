package net.power_upgrade.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.dungeon_difficulty.logic.Difficulty;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.power_upgrade.helper.DungeonDifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(VaultBlockEntity.Server.class)
public abstract class VaultBlockEntityMixin {

    @ModifyReturnValue(
            method = "generateLoot(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/block/vault/VaultConfig;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)Ljava/util/List;",
            at = @At("RETURN")
    )
    private static List<ItemStack> onGenerateLoot(
            List<ItemStack> original,
            ServerWorld world, VaultConfig config, BlockPos pos, PlayerEntity player) {

        boolean ominous = world.getBlockState(pos).get(VaultBlock.OMINOUS);

        Difficulty difficulty = DungeonDifficultyHelper.getDifficultyAt(world, pos);
        if (difficulty == null || difficulty.rewardLevel() <= 0) return original;

        LootTable bonusTable = resolveBonusVaultLootTable(world, difficulty,ominous);
        if (bonusTable == null) return original;

        LootContextParameterSet lootContext = new LootContextParameterSet.Builder(world)
                .add(LootContextParameters.ORIGIN, pos.toCenterPos())
                .add(LootContextParameters.THIS_ENTITY, player)
                .luck(player.getLuck())
                .build(LootContextTypes.VAULT);

        original.addAll(bonusTable.generateLoot(lootContext));
        return original;
    }

    @ModifyReturnValue(
            method = "generateDisplayItem(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/item/ItemStack;",
            at = @At("RETURN")
    )
    private static ItemStack onGenerateDisplayItem(ItemStack original, ServerWorld world, BlockPos pos, RegistryKey<LootTable> lootTableKey) {
        boolean ominous = world.getBlockState(pos).get(VaultBlock.OMINOUS);

        Difficulty difficulty = DungeonDifficultyHelper.getDifficultyAt(world, pos);
        if (difficulty == null || difficulty.rewardLevel() <= 0) return original;

        LootTable bonusTable = resolveBonusVaultLootTable(world, difficulty, ominous);
        if (bonusTable == null) return original;

        LootContextParameterSet lootContext = new LootContextParameterSet.Builder(world)
                .add(LootContextParameters.ORIGIN, pos.toCenterPos())
                .build(LootContextTypes.VAULT);

        List<ItemStack> bonusItems = bonusTable.generateLoot(lootContext, world.getRandom());
        if (bonusItems.isEmpty()) return original;

        List<ItemStack> candidates = new ArrayList<>(bonusItems);
        if (!original.isEmpty()) candidates.add(original);

        return Util.getRandom(candidates, world.getRandom());
    }

    @Unique
    private static LootTable resolveBonusVaultLootTable(ServerWorld world, Difficulty difficulty, boolean ominous) {
        Identifier tableId = Identifier.of(
                "power_upgrade",
                (ominous ? "bonus/vaults_ominous/" : "bonus/vaults/")
                        + difficulty.type().name + "/level_" + difficulty.rewardLevel()
        );
        RegistryKey<LootTable> tableKey = RegistryKey.of(RegistryKeys.LOOT_TABLE, tableId);
        LootTable table = world.getServer().getReloadableRegistries().getLootTable(tableKey);
        return table == LootTable.EMPTY ? null : table;
    }
}
