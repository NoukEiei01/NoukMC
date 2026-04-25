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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.nsyl.client.hacks.autofarm.AutoFarmPlantType;
import com.nsyl.client.settings.PlantTypeSetting;
import com.nsyl.client.util.BlockUtils;

public final class SugarCanePlantType extends AutoFarmPlantType
{
	@Override
	public final boolean isReplantingSpot(BlockPos pos, BlockState state)
	{
		if(!state.is(Blocks.SUGAR_CANE))
			return false;
		
		return !BlockUtils.getState(pos.below()).is(Blocks.SUGAR_CANE)
			&& hasPlantingSurface(pos);
	}
	
	@Override
	public final boolean hasPlantingSurface(BlockPos pos)
	{
		BlockPos floorPos = pos.below();
		BlockState floor = BlockUtils.getState(floorPos);
		if(!floor.is(BlockTags.DIRT) && !floor.is(BlockTags.SAND))
			return false;
		
		for(Direction side : Direction.Plane.HORIZONTAL)
		{
			BlockState floorNeighbor =
				BlockUtils.getState(floorPos.relative(side));
			
			if(floorNeighbor.getFluidState().is(FluidTags.WATER)
				|| floorNeighbor.is(Blocks.FROSTED_ICE))
				return true;
		}
		
		return false;
	}
	
	@Override
	public Item getSeedItem()
	{
		return Items.SUGAR_CANE;
	}
	
	@Override
	public boolean shouldHarvestByMining(BlockPos pos, BlockState state)
	{
		if(!state.is(Blocks.SUGAR_CANE))
			return false;
		
		BlockPos below = pos.below();
		return isReplantingSpot(below, BlockUtils.getState(below));
	}
	
	@Override
	protected PlantTypeSetting createSetting()
	{
		return new PlantTypeSetting("Sugar Cane", Items.SUGAR_CANE, true, true);
	}
}
