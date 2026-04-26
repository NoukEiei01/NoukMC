/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks.chestesp.groups;

import java.awt.Color;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import com.nsyl.client.hacks.chestesp.ChestEspBlockGroup;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.settings.ColorSetting;
import com.nsyl.client.util.LootrModCompat;

public final class NormalChestsGroup extends ChestEspBlockGroup
{
	@Override
	protected CheckboxSetting createIncludeSetting()
	{
		return new CheckboxSetting("Include normal chests", true);
	}
	
	@Override
	protected ColorSetting createColorSetting()
	{
		return new ColorSetting("Chest color",
			"Normal chests will be highlighted in this color.", Color.GREEN);
	}
	
	@Override
	protected boolean matches(BlockEntity be)
	{
		return be instanceof ChestBlockEntity
			&& !(be instanceof TrappedChestBlockEntity)
			&& !LootrModCompat.isLootrTrappedChest(be);
	}
}
