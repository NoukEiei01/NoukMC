/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.MapCodec;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.nsyl.client.NsylClient;
import com.nsyl.client.event.EventManager;
import com.nsyl.client.events.GetAmbientOcclusionLightLevelListener.GetAmbientOcclusionLightLevelEvent;
import com.nsyl.client.events.IsNormalCubeListener.IsNormalCubeEvent;
import com.nsyl.client.hack.HackList;
import com.nsyl.client.hacks.HandNoClipHack;

@Mixin(BlockStateBase.class)
public abstract class AbstractBlockStateMixin
	extends StateHolder<Block, BlockState>
{
	private AbstractBlockStateMixin(NsylClient wurst, Block owner,
		Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap,
		MapCodec<BlockState> codec)
	{
		super(owner, propertyMap, codec);
	}
	
	@Inject(at = @At("TAIL"),
		method = "isCollisionShapeFullBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z",
		cancellable = true)
	private void onIsFullCube(BlockGetter world, BlockPos pos,
		CallbackInfoReturnable<Boolean> cir)
	{
		IsNormalCubeEvent event = new IsNormalCubeEvent();
		EventManager.fire(event);
		
		cir.setReturnValue(cir.getReturnValue() && !event.isCancelled());
	}
	
	@Inject(at = @At("TAIL"),
		method = "getShadeBrightness(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F",
		cancellable = true)
	private void onGetAmbientOcclusionLightLevel(BlockGetter blockView,
		BlockPos blockPos, CallbackInfoReturnable<Float> cir)
	{
		GetAmbientOcclusionLightLevelEvent event =
			new GetAmbientOcclusionLightLevelEvent((BlockState)(Object)this,
				cir.getReturnValueF());
		
		EventManager.fire(event);
		cir.setReturnValue(event.getLightLevel());
	}
	
	@Inject(at = @At("HEAD"),
		method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
		cancellable = true)
	private void onGetOutlineShape(BlockGetter view, BlockPos pos,
		CollisionContext context, CallbackInfoReturnable<VoxelShape> cir)
	{
		if(context == CollisionContext.empty())
			return;
		
		HackList hax = NsylClient.INSTANCE.getHax();
		if(hax == null)
			return;
		
		HandNoClipHack handNoClipHack = hax.handNoClipHack;
		if(!handNoClipHack.isEnabled() || handNoClipHack.isBlockInList(pos))
			return;
		
		cir.setReturnValue(Shapes.empty());
	}
	
	@Inject(at = @At("HEAD"),
		method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
		cancellable = true)
	private void onGetCollisionShape(BlockGetter world, BlockPos pos,
		CollisionContext context, CallbackInfoReturnable<VoxelShape> cir)
	{
		if(getFluidState().isEmpty())
			return;
		
		HackList hax = NsylClient.INSTANCE.getHax();
		if(hax == null || !hax.jesusHack.shouldBeSolid())
			return;
		
		cir.setReturnValue(Shapes.block());
		cir.cancel();
	}
	
	@Shadow
	public abstract FluidState getFluidState();
}
