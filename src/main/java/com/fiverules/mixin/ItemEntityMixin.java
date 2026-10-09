package com.fiverules.mixin;

import com.fiverules.rules.ReadBookRule;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Unique
	private ItemStack fiverules$stackBefore = ItemStack.EMPTY;

	@Shadow
	public abstract ItemStack getStack();

	@Inject(method = "onPlayerCollision", at = @At("HEAD"))
	private void fiverules$beforePickup(PlayerEntity player, CallbackInfo ci) {
		fiverules$stackBefore = getStack().copy();
	}

	@Inject(method = "onPlayerCollision", at = @At("RETURN"))
	private void fiverules$afterPickup(PlayerEntity player, CallbackInfo ci) {
		ItemEntity self = (ItemEntity) (Object) this;
		boolean pickedUp = self.isRemoved() || getStack().getCount() < fiverules$stackBefore.getCount();
		if (pickedUp && player instanceof ServerPlayerEntity serverPlayer && !fiverules$stackBefore.isEmpty()) {
			ReadBookRule.onPickup(serverPlayer, fiverules$stackBefore);
		}
		fiverules$stackBefore = ItemStack.EMPTY;
	}
}
