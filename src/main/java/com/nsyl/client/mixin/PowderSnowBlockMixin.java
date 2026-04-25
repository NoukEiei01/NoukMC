/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.PowderSnowBlock;
import com.nsyl.client.NsylClient;

@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowBlockMixin extends Block implements BucketPickup
{
	private PowderSnowBlockMixin(NsylClient wurst, Properties settings)
	{
		super(settings);
	}
	
	@Inject(at = @At("HEAD"),
		method = "canEntityWalkOnPowderSnow(Lnet/minecraft/world/entity/Entity;)Z",
		cancellable = true)
	private static void onCanWalkOnPowderSnow(Entity entity,
		CallbackInfoReturnable<Boolean> cir)
	{
		if(!NsylClient.INSTANCE.getHax().snowShoeHack.isEnabled())
			return;
		
		if(entity == NsylClient.MC.player)
			cir.setReturnValue(true);
	}
}
