/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.commands;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import com.nsyl.client.command.CmdError;
import com.nsyl.client.command.CmdException;
import com.nsyl.client.command.CmdSyntaxError;
import com.nsyl.client.command.Command;
import com.nsyl.client.util.ChatUtils;

public final class EnchantCmd extends Command
{
	public EnchantCmd()
	{
		super("enchant", "Enchants an item with everything,\n"
			+ "except for silk touch and curses.", ".enchant");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(!MC.player.getAbilities().instabuild)
			throw new CmdError("Creative mode only.");
		
		if(args.length > 1)
			throw new CmdSyntaxError();
		
		enchant(getHeldItem(), 127);
		ChatUtils.message("Item enchanted.");
	}
	
	private ItemStack getHeldItem() throws CmdError
	{
		ItemStack stack = MC.player.getMainHandItem();
		
		if(stack.isEmpty())
			stack = MC.player.getOffhandItem();
		
		if(stack.isEmpty())
			throw new CmdError("There is no item in your hand.");
		
		return stack;
	}
	
	private void enchant(ItemStack stack, int level)
	{
		RegistryAccess drm = MC.level.registryAccess();
		Registry<Enchantment> registry =
			drm.lookupOrThrow(Registries.ENCHANTMENT);
		
		for(Holder<Enchantment> entry : registry.asHolderIdMap())
		{
			// Skip curses
			if(entry.is(EnchantmentTags.CURSE))
				continue;
			
			// Skip Silk Touch so it doesn't remove Fortune
			if(entry.unwrapKey().orElse(null) == Enchantments.SILK_TOUCH)
				continue;
			
			// Limit Quick Charge to level 5 so it doesn't break
			if(entry.unwrapKey().orElse(null) == Enchantments.QUICK_CHARGE)
			{
				stack.enchant(entry, Math.min(level, 5));
				continue;
			}
			
			stack.enchant(entry, level);
		}
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Enchant Held Item";
	}
	
	@Override
	public void doPrimaryAction()
	{
		CLIENT.getCmdProcessor().process("enchant");
	}
}
