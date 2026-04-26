/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import net.minecraft.world.entity.player.Inventory;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.hack.Hack;

@SearchTags({"auto switch"})
public final class AutoSwitchHack extends Hack implements UpdateListener
{
	public AutoSwitchHack()
	{
		super("AutoSwitch");
		setCategory(Category.ITEMS);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		Inventory inventory = MC.player.getInventory();
		
		if(inventory.selected == 8)
			inventory.selected = 0;
		else
			inventory.selected++;
	}
}
