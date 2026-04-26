/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks.autofarm.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.nsyl.client.hacks.autofarm.AutoFarmPlantType;
import com.nsyl.client.settings.PlantTypeSetting;
import com.nsyl.client.util.BlockUtils;

public final class PumpkinPlantType extends AutoFarmPlantType
{
	@Override
	public final boolean isReplantingSpot(BlockPos pos, BlockState state)
	{
		return state.is(Blocks.PUMPKIN_STEM)
			|| state.is(Blocks.ATTACHED_PUMPKIN_STEM);
	}
	
	@Override
	public final boolean hasPlantingSurface(BlockPos pos)
	{
		return BlockUtils.getState(pos.below()).is(Blocks.FARMLAND);
	}
	
	@Override
	public Item getSeedItem()
	{
		return Items.PUMPKIN_SEEDS;
	}
	
	@Override
	public boolean shouldHarvestByMining(BlockPos pos, BlockState state)
	{
		return state.is(Blocks.PUMPKIN);
	}
	
	@Override
	protected PlantTypeSetting createSetting()
	{
		return new PlantTypeSetting("Pumpkins", Items.PUMPKIN, true, true);
	}
}
