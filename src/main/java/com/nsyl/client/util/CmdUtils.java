/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.util;

import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.nsyl.client.Feature;
import com.nsyl.client.NsylClient;
import com.nsyl.client.command.CmdError;
import com.nsyl.client.command.CmdSyntaxError;
import com.nsyl.client.settings.Setting;

public enum CmdUtils
{
	;
	
	private static final Minecraft MC = NsylClient.MC;
	
	public static Feature findFeature(String name) throws CmdError
	{
		Stream<Feature> stream =
			NsylClient.INSTANCE.getNavigator().getList().stream();
		stream = stream.filter(f -> name.equalsIgnoreCase(f.getName()));
		Feature feature = stream.findFirst().orElse(null);
		
		if(feature == null)
			throw new CmdError(
				"A feature named \"" + name + "\" could not be found.");
		
		return feature;
	}
	
	public static Setting findSetting(Feature feature, String name)
		throws CmdError
	{
		name = name.replace("_", " ").toLowerCase();
		Setting setting = feature.getSettings().get(name);
		
		if(setting == null)
			throw new CmdError("A setting named \"" + name
				+ "\" could not be found in " + feature.getName() + ".");
		
		return setting;
	}
	
	public static Item parseItem(String nameOrId) throws CmdSyntaxError
	{
		Item item = ItemUtils.getItemFromNameOrID(nameOrId);
		
		if(item == null)
			throw new CmdSyntaxError(
				"\"" + nameOrId + "\" is not a valid item.");
		
		return item;
	}
	
	public static void giveItem(ItemStack stack) throws CmdError
	{
		Inventory inventory = MC.player.getInventory();
		int slot = inventory.getFreeSlot();
		if(slot < 0)
			throw new CmdError("Cannot give item. Your inventory is full.");
		
		InventoryUtils.setCreativeStack(slot, stack);
	}
}
