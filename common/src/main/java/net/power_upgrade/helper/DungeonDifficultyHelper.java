package net.power_upgrade.helper;

import net.dungeon_difficulty.logic.Difficulty;
import net.dungeon_difficulty.logic.PatternMatching;
import net.dungeon_difficulty.logic.ScalingGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class DungeonDifficultyHelper {

    private DungeonDifficultyHelper() {}

    @Nullable
    public static Difficulty getDifficultyAt(ServerWorld world, BlockPos pos) {
        PatternMatching.LocationData locationData = PatternMatching.LocationData.create(world, pos);
        Difficulty d = PatternMatching.getDifficulty(locationData, world);
        return (d != null && d.isValid()) ? d : null;
    }

    @Nullable
    public static Difficulty getDifficultyAt(ServerWorld world, BlockPos pos, @Nullable Identifier entityTypeId) {
        PatternMatching.LocationData locationData = PatternMatching.LocationData.create(world, pos);
        Difficulty d = PatternMatching.getDifficulty(locationData, entityTypeId, world);
        return (d != null && d.isValid()) ? d : null;
    }

    @Nullable
    public static Difficulty getDifficultyForEntity(ServerWorld world, LivingEntity entity) {
        PatternMatching.LocationData locationData =
                PatternMatching.LocationData.create(world, entity.getBlockPos());

        Identifier entityTypeId = Registries.ENTITY_TYPE.getId(entity.getType());
        Identifier lootTableId = entity.getLootTable().getValue();

        Difficulty byType = PatternMatching.getDifficulty(locationData, entityTypeId, world);
        PatternMatching.DifficultySearchResult byLootResult =
                PatternMatching.getDifficultyResult(locationData, lootTableId, ScalingGoal.LOOT, world);
        Difficulty byLoot = byLootResult != null ? byLootResult.difficulty() : null;

        Difficulty best = higherLevel(byType, byLoot);
        return (best != null && best.isValid()) ? best : null;
    }

    @Nullable
    private static Difficulty higherLevel(@Nullable Difficulty a, @Nullable Difficulty b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.level() >= b.level() ? a : b;
    }

    @Nullable
    public static PatternMatching.DifficultySearchResult getDifficultyResult(
            ServerWorld world, BlockPos pos,
            @Nullable Identifier lootTableId,
            ScalingGoal goal) {
        PatternMatching.LocationData locationData = PatternMatching.LocationData.create(world, pos);
        return PatternMatching.getDifficultyResult(locationData, lootTableId, goal, world);
    }

    public static int getRewardLevel(ServerWorld world, BlockPos pos) {
        Difficulty d = getDifficultyAt(world, pos);
        return d != null ? d.rewardLevel() : 0;
    }

    public static int getEntityLevel(ServerWorld world, LivingEntity entity) {
        Difficulty d = getDifficultyForEntity(world, entity);
        return d != null ? d.entityLevel() : 0;
    }

    @Nullable
    public static String getDifficultyTypeName(ServerWorld world, BlockPos pos) {
        Difficulty d = getDifficultyAt(world, pos);
        return d != null ? d.type().name : null;
    }
}