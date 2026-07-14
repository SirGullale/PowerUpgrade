package net.power_upgrade.mixin;

import net.dungeon_difficulty.logic.Difficulty;
import net.power_upgrade.helper.DungeonDifficultyHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
            method = "drop(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)V",
            at = @At("TAIL")
    )
    private void onDrop(ServerWorld serverWorld, DamageSource damageSource, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (self instanceof net.minecraft.entity.player.PlayerEntity) return;

        Difficulty difficulty = DungeonDifficultyHelper.getDifficultyForEntity(serverWorld, self);

        if (difficulty == null || difficulty.entityLevel() <= 0) return;

        dropBonusLoot(self, serverWorld, damageSource, difficulty);
    }

    @Unique
    private static void dropBonusLoot(
            LivingEntity entity,
            ServerWorld world,
            DamageSource damageSource,
            Difficulty difficulty) {

        LootTable bonusTable = resolveBonusMobLootTable(world, entity, difficulty);
        if (bonusTable == null) return;

        LootContextParameterSet.Builder contextBuilder =
                new LootContextParameterSet.Builder(world)
                        .add(LootContextParameters.THIS_ENTITY, entity)
                        .add(LootContextParameters.ORIGIN, entity.getPos())
                        .add(LootContextParameters.DAMAGE_SOURCE, damageSource)
                        .addOptional(LootContextParameters.ATTACKING_ENTITY, damageSource.getAttacker())
                        .addOptional(LootContextParameters.DIRECT_ATTACKING_ENTITY, damageSource.getSource());

        if (damageSource.getAttacker() instanceof net.minecraft.entity.player.PlayerEntity killer) {
            contextBuilder.addOptional(LootContextParameters.LAST_DAMAGE_PLAYER, killer);
        }

        List<ItemStack> bonusItems = bonusTable.generateLoot(
                contextBuilder.build(LootContextTypes.ENTITY)
        );

        for (ItemStack bonus : bonusItems) {
            if (!bonus.isEmpty()) {
                entity.dropStack(bonus);
            }
        }
    }

    @Unique
    private static LootTable resolveBonusMobLootTable(
            ServerWorld world,
            LivingEntity entity,
            Difficulty difficulty) {

        Identifier tableId = Identifier.of(
                "power_upgrade",
                "bonus/entities/" + difficulty.type().name + "/level_" + difficulty.entityLevel()
        );
        RegistryKey<LootTable> tableKey = RegistryKey.of(RegistryKeys.LOOT_TABLE, tableId);
        LootTable table = world.getServer().getReloadableRegistries().getLootTable(tableKey);

        return table == LootTable.EMPTY ? null : table;
    }
}