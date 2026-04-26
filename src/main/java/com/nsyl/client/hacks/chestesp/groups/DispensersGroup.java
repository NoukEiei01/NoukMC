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
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import com.nsyl.client.hacks.chestesp.ChestEspBlockGroup;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.settings.ColorSetting;

public final class DispensersGroup extends ChestEspBlockGroup
{
	@Override
	protected CheckboxSetting createIncludeSetting()
	{
		return new CheckboxSetting("Include dispensers", false);
	}
	
	@Override
	protected ColorSetting createColorSetting()
	{
		return new ColorSetting("Dispenser color",
			"Dispensers will be highlighted in this color.",
			new Color(0xFF8000));
	}
	
	@Override
	protected boolean matches(BlockEntity be)
	{
		return be instanceof DispenserBlockEntity
			&& !(be instanceof DropperBlockEntity);
	}
}
