package com.fiverules.rules;

import com.fiverules.logic.Deadlines;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/** Rule 11: a door a player opens and leaves open for 30 seconds lets a creeper in. */
public final class DoorRule {
	private static final long OPEN_TICKS = 30 * 20;
	private static final Deadlines<DoorKey, UUID> OPEN_DOORS = new Deadlines<>();

	private DoorRule() {
	}

	private record DoorKey(ServerWorld world, BlockPos pos) {
	}

	public static void register() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!(world instanceof ServerWorld serverWorld)
					|| hand != Hand.MAIN_HAND
					|| player.shouldCancelInteraction()
					|| Punish.isExempt(player)) {
				return ActionResult.PASS;
			}
			BlockPos pos = hitResult.getBlockPos();
			BlockState state = serverWorld.getBlockState(pos);
			if (!(state.getBlock() instanceof DoorBlock) || !DoorBlock.canOpenByHand(serverWorld, pos)) {
				return ActionResult.PASS;
			}
			DoorKey key = new DoorKey(serverWorld, lowerHalf(pos, state));
			// The event runs before the door toggles: a closed door is about to be opened.
			if (state.get(DoorBlock.OPEN)) {
				OPEN_DOORS.cancel(key);
			} else {
				OPEN_DOORS.schedule(key, player.getUuid(), serverWorld.getServer().getTicks() + OPEN_TICKS);
			}
			return ActionResult.PASS;
		});

		ServerTickEvents.END_SERVER_TICK.register(DoorRule::tick);
	}

	private static BlockPos lowerHalf(BlockPos pos, BlockState state) {
		return state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos.toImmutable();
	}

	private static void tick(MinecraftServer server) {
		for (DoorKey key : OPEN_DOORS.keys()) {
			BlockState state = key.world().getBlockState(key.pos());
			if (!(state.getBlock() instanceof DoorBlock) || !state.get(DoorBlock.OPEN)) {
				OPEN_DOORS.cancel(key);
			}
		}
		Set<DoorKey> handled = new HashSet<>();
		for (var entry : OPEN_DOORS.popExpiredEntries(server.getTicks())) {
			DoorKey key = entry.getKey();
			if (!handled.add(key)) {
				continue;
			}
			// A double door is one door: forget its other half so it doesn't spawn a second creeper.
			for (Direction side : Direction.Type.HORIZONTAL) {
				DoorKey neighbor = new DoorKey(key.world(), key.pos().offset(side));
				OPEN_DOORS.cancel(neighbor);
				handled.add(neighbor);
			}
			ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getValue());
			if (player == null || Punish.isExempt(player)) {
				continue;
			}
			spawnCreeper(key.world(), key.pos(), key.world().getBlockState(key.pos()).get(DoorBlock.FACING));
			Punish.announce(player, "RULE 11: You left the door open! Something came in...");
		}
	}

	private static void spawnCreeper(ServerWorld world, BlockPos door, Direction facing) {
		BlockPos front = door.offset(facing);
		BlockPos back = door.offset(facing.getOpposite());
		// "Outside" is the side that can see the sky.
		BlockPos spawn = world.isSkyVisible(back) && !world.isSkyVisible(front) ? back : front;
		CreeperEntity creeper = EntityType.CREEPER.create(world);
		if (creeper == null) {
			return;
		}
		creeper.refreshPositionAndAngles(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, facing.asRotation(), 0.0F);
		world.spawnEntity(creeper);
	}
}
