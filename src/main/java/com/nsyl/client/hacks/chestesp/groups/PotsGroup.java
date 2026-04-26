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
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import com.nsyl.client.hacks.chestesp.ChestEspBlockGroup;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.settings.ColorSetting;

public final class PotsGroup extends ChestEspBlockGroup
{
	@Override
	protected CheckboxSetting createIncludeSetting()
	{
		return new CheckboxSetting("Include pots", false);
	}
	
	@Override
	protected ColorSetting createColorSetting()
	{
		return new ColorSetting("Pots color",
			"Decorated pots will be highlighted in this color.", Color.GREEN);
	}
	
	@Override
	protected boolean matches(BlockEntity be)
	{
		return be instanceof DecoratedPotBlockEntity;
	}
}
