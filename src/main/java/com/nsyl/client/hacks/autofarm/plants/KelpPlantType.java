/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks.autofarm.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import com.nsyl.client.NsylClient;
import com.nsyl.client.hacks.autofarm.AutoFarmPlantType;
import com.nsyl.client.settings.PlantTypeSetting;
import com.nsyl.client.util.BlockUtils;

public final class KelpPlantType extends AutoFarmPlantType
{
	// KELP is the tip, KELP_PLANT is any other part below that.
	// Either one can be the stem, depending on the size of the plant.
	
	@Override
	public final boolean isReplantingSpot(BlockPos pos, BlockState state)
	{
		if(!state.is(Blocks.KELP) && !state.is(Blocks.KELP_PLANT))
			return false;
		
		BlockState floor = BlockUtils.getState(pos.below());
		return !floor.is(Blocks.KELP) && !floor.is(Blocks.KELP_PLANT)
			&& hasPlantingSurface(pos);
	}
	
	@Override
	public final boolean hasPlantingSurface(BlockPos pos)
	{
		FluidState fluid = BlockUtils.getState(pos).getFluidState();
		if(!fluid.is(FluidTags.WATER) || fluid.getAmount() != 8)
			return false;
		
		BlockState floor = BlockUtils.getState(pos.below());
		return !floor.is(Blocks.MAGMA_BLOCK)
			&& floor.isFaceSturdy(NsylClient.MC.level, pos, Direction.UP);
	}
	
	@Override
	public Item getSeedItem()
	{
		return Items.KELP;
	}
	
	@Override
	public boolean shouldHarvestByMining(BlockPos pos, BlockState state)
	{
		if(!state.is(Blocks.KELP) && !state.is(Blocks.KELP_PLANT))
			return false;
		
		BlockPos below = pos.below();
		return isReplantingSpot(below, BlockUtils.getState(below));
	}
	
	@Override
	protected PlantTypeSetting createSetting()
	{
		return new PlantTypeSetting("Kelp", Items.KELP, true, true);
	}
}
