package net.power_upgrade.helper;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class LootableContainerMixinHelper {

    private LootableContainerMixinHelper() {}

    public static final ThreadLocal<PendingContext> PENDING = new ThreadLocal<>();

    public record PendingContext(ServerWorld world, BlockPos pos) {}
}