/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks.treebot;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import com.nsyl.client.util.BlockUtils;

public enum TreeBotUtils
{
	;
	
	public static boolean isLog(BlockPos pos)
	{
		return BlockUtils.getState(pos).is(BlockTags.LOGS);
	}
	
	public static boolean isLeaves(BlockPos pos)
	{
		BlockState state = BlockUtils.getState(pos);
		return state.is(BlockTags.LEAVES) || state.is(BlockTags.WART_BLOCKS);
	}
}
