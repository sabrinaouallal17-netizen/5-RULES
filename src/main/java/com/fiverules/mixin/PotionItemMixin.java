package com.fiverules.mixin;

import com.fiverules.rules.HydrationRule;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin {
	@Inject(method = "finishUsing", at = @At("HEAD"))
	private void fiverules$onDrink(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
		PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
		if (user instanceof ServerPlayerEntity player && contents != null && contents.matches(Potions.WATER)) {
			HydrationRule.onDrinkWater(player);
		}
	}
}
