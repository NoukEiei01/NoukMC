/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.settings.filters;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.allay.Allay;

public final class FilterAllaysSetting extends EntityFilterCheckbox
{
	public FilterAllaysSetting(String description, boolean checked)
	{
		super("Filter allays", description, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		return !(e instanceof Allay);
	}
	
	public static FilterAllaysSetting genericCombat(boolean checked)
	{
		return new FilterAllaysSetting(
			"description.nsyl.setting.generic.filter_allays_combat", checked);
	}
	
	public static FilterAllaysSetting genericVision(boolean checked)
	{
		return new FilterAllaysSetting(
			"description.nsyl.setting.generic.filter_allays_vision", checked);
	}
}
