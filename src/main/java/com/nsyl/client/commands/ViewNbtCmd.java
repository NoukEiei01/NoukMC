/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.commands;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import com.nsyl.client.SearchTags;
import com.nsyl.client.command.CmdError;
import com.nsyl.client.command.CmdException;
import com.nsyl.client.command.CmdSyntaxError;
import com.nsyl.client.command.Command;
import com.nsyl.client.util.ChatUtils;

@SearchTags({"view nbt", "NBTViewer", "nbt viewer"})
public final class ViewNbtCmd extends Command
{
	public ViewNbtCmd()
	{
		super("viewnbt", "Shows you the NBT data of an item.", ".viewnbt",
			"Copy to clipboard: .viewnbt copy");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		LocalPlayer player = MC.player;
		ItemStack stack = player.getInventory().getSelected();
		if(stack.isEmpty())
			throw new CmdError("You must hold an item in your main hand.");
		
		CompoundTag tag =
			stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
				.copyTag();
		String nbtString = tag.getAsString();
		
		switch(String.join(" ", args).toLowerCase())
		{
			case "":
			ChatUtils.message("NBT: " + nbtString);
			break;
			
			case "copy":
			MC.keyboardHandler.setClipboard(nbtString);
			ChatUtils.message("NBT data copied to clipboard.");
			break;
			
			default:
			throw new CmdSyntaxError();
		}
	}
}
