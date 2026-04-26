/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.commands;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import com.nsyl.client.command.CmdError;
import com.nsyl.client.command.CmdException;
import com.nsyl.client.command.CmdSyntaxError;
import com.nsyl.client.command.Command;
import com.nsyl.client.util.ChatUtils;
import com.nsyl.client.util.InventoryUtils;

public final class RepairCmd extends Command
{
	public RepairCmd()
	{
		super("repair", "Repairs the held item. Requires creative mode.",
			".repair");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length > 0)
			throw new CmdSyntaxError();
		
		LocalPlayer player = MC.player;
		
		if(!player.getAbilities().instabuild)
			throw new CmdError("Creative mode only.");
		
		int slot = player.getInventory().selected;
		ItemStack stack = getHeldStack(player);
		stack.setDamageValue(0);
		InventoryUtils.setCreativeStack(slot, stack);
		
		ChatUtils.message("Item repaired.");
	}
	
	private ItemStack getHeldStack(LocalPlayer player) throws CmdError
	{
		ItemStack stack = player.getInventory().getSelected();
		
		if(stack.isEmpty())
			throw new CmdError("You need an item in your hand.");
		
		if(!stack.isDamageableItem())
			throw new CmdError("This item can't take damage.");
		
		if(!stack.isDamaged())
			throw new CmdError("This item is not damaged.");
		
		return stack;
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Repair Current Item";
	}
	
	@Override
	public void doPrimaryAction()
	{
		CLIENT.getCmdProcessor().process("repair");
	}
}
