/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import com.nsyl.client.Category;
import com.nsyl.client.events.RightClickListener;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.settings.SliderSetting;
import com.nsyl.client.settings.SliderSetting.ValueDisplay;

public final class ThrowHack extends Hack implements RightClickListener
{
	private final SliderSetting amount = new SliderSetting("Amount",
		"Amount of uses per click.", 16, 2, 1000000, 1, ValueDisplay.INTEGER);
	
	public ThrowHack()
	{
		super("Throw");
		
		setCategory(Category.OTHER);
		addSetting(amount);
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + amount.getValueString() + "]";
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(RightClickListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(RightClickListener.class, this);
	}
	
	@Override
	public void onRightClick(RightClickEvent event)
	{
		if(MC.rightClickDelay > 0)
			return;
		
		if(!MC.options.keyUse.isDown())
			return;
		
		for(int i = 0; i < amount.getValueI(); i++)
		{
			if(MC.hitResult.getType() == HitResult.Type.BLOCK)
			{
				BlockHitResult hitResult = (BlockHitResult)MC.hitResult;
				IMC.getInteractionManager().rightClickBlock(
					hitResult.getBlockPos(), hitResult.getDirection(),
					hitResult.getLocation());
			}
			
			IMC.getInteractionManager().rightClickItem();
		}
	}
}
