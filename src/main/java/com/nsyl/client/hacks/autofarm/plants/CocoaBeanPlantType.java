/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks.autofarm.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.nsyl.client.hacks.autofarm.AutoFarmPlantType;
import com.nsyl.client.settings.PlantTypeSetting;
import com.nsyl.client.util.BlockUtils;

public final class CocoaBeanPlantType extends AutoFarmPlantType
{
	@Override
	public final boolean isReplantingSpot(BlockPos pos, BlockState state)
	{
		return state.getBlock() instanceof CocoaBlock;
	}
	
	@Override
	public final boolean hasPlantingSurface(BlockPos pos)
	{
		return Direction.Plane.HORIZONTAL.stream().map(pos::relative)
			.map(BlockUtils::getState)
			.anyMatch(neighbor -> neighbor.is(BlockTags.JUNGLE_LOGS));
	}
	
	@Override
	public Item getSeedItem()
	{
		return Items.COCOA_BEANS;
	}
	
	@Override
	public boolean shouldHarvestByMining(BlockPos pos, BlockState state)
	{
		if(!(state.getBlock() instanceof CocoaBlock))
			return false;
		
		return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
	}
	
	@Override
	protected PlantTypeSetting createSetting()
	{
		return new PlantTypeSetting("Cocoa Beans", Items.COCOA_BEANS, true,
			true);
	}
}
