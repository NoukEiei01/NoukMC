/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks.chestesp.groups;

import java.awt.Color;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import com.nsyl.client.hacks.chestesp.ChestEspEntityGroup;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.settings.ColorSetting;

public final class HopperCartsGroup extends ChestEspEntityGroup
{
	@Override
	protected CheckboxSetting createIncludeSetting()
	{
		return new CheckboxSetting("Include hopper carts", false);
	}
	
	@Override
	protected ColorSetting createColorSetting()
	{
		return new ColorSetting("Hopper cart color",
			"Minecarts with hoppers will be highlighted in this color.",
			Color.YELLOW);
	}
	
	@Override
	protected boolean matches(Entity e)
	{
		return e instanceof MinecartHopper;
	}
}
