/*
 * Copyright (c) 2025 NSYL and contributors.
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

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import com.nsyl.client.NsylClient;

@Mixin(ItemBlockRenderTypes.class)
public abstract class RenderLayersMixin
{
	/**
	 * Puts all blocks on the translucent layer if Opacity X-Ray is enabled.
	 */
	@Inject(at = @At("HEAD"),
		method = "getChunkRenderType(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/RenderType;",
		cancellable = true)
	private static void onGetBlockLayer(BlockState state,
		CallbackInfoReturnable<RenderType> cir)
	{
		if(!NsylClient.INSTANCE.getHax().xRayHack.isOpacityMode())
			return;
		
		cir.setReturnValue(RenderType.translucent());
	}
}
