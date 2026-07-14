package net.power_upgrade.mixin;

import net.dungeon_difficulty.logic.Difficulty;
import net.power_upgrade.helper.DungeonDifficultyHelper;
import net.power_upgrade.helper.LootableContainerMixinHelper;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mixin(LootableInventory.class)
public interface LootableInventoryMixin {

    @Inject(
            method = "generateLoot(Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At("HEAD")
    )
    default void onGenerateLootHead(PlayerEntity player, CallbackInfo ci) {

        if (!(this instanceof LootableContainerBlockEntity be)) return;

        if (((LootableInventory) this).getLootTable() == null) return;

        World world = be.getWorld();
        if (world == null || world.isClient()) return;

        LootableContainerMixinHelper.PENDING.set(
                new LootableContainerMixinHelper.PendingContext((ServerWorld) world, be.getPos())
        );
    }

    @Inject(
            method = "generateLoot(Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At("TAIL")
    )
    default void onGenerateLootTail(PlayerEntity player, CallbackInfo ci) {
        LootableContainerMixinHelper.PendingContext ctx = LootableContainerMixinHelper.PENDING.get();
        if (ctx == null) return;
        LootableContainerMixinHelper.PENDING.remove();

        if (!(this instanceof LootableContainerBlockEntity be)) return;

        Difficulty difficulty =
                DungeonDifficultyHelper.getDifficultyAt(ctx.world(), ctx.pos());

        if (difficulty == null || difficulty.rewardLevel() <= 0) return;

        injectBonusLoot(be, ctx.world(), difficulty);
    }

    @Unique
    private static void injectBonusLoot(
            LootableContainerBlockEntity be,
            ServerWorld world,
            Difficulty difficulty) {

        LootTable bonusTable = resolveBonusLootTable(world, difficulty);
        if (bonusTable == null) return;

        LootContextParameterSet lootContext = new LootContextParameterSet.Builder(world)
                .add(LootContextParameters.ORIGIN, be.getPos().toCenterPos())
                .build(LootContextTypes.CHEST);

        List<ItemStack> bonusItems = bonusTable.generateLoot(lootContext);

        insertRandomly(be, bonusItems, world.getRandom());
    }

    @Unique
    private static LootTable resolveBonusLootTable(
            ServerWorld world,
            Difficulty difficulty) {

        Identifier tableId = Identifier.of(
                "power_upgrade",
                "bonus/chests/" + difficulty.type().name + "/level_" + difficulty.rewardLevel()
        );
        RegistryKey<LootTable> tableKey = RegistryKey.of(RegistryKeys.LOOT_TABLE, tableId);
        LootTable table = world.getServer().getReloadableRegistries().getLootTable(tableKey);

        return table == LootTable.EMPTY ? null : table;
    }

    @Unique
    private static void insertRandomly(
            LootableContainerBlockEntity be,
            List<ItemStack> items,
            Random random) {

        List<Integer> emptySlots = IntStream.range(0, be.size())
                .filter(i -> be.getStack(i).isEmpty())
                .boxed()
                .collect(Collectors.toList());

        for (ItemStack stack : items) {
            if (stack.isEmpty() || emptySlots.isEmpty()) {
                return;
            }

            int slot = emptySlots.remove(
                    random.nextInt(emptySlots.size())
            );

            be.setStack(slot, stack.copy());
        }
    }
}