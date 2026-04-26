/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.events.IsPlayerInWaterListener;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.events.VelocityFromFluidListener;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.settings.CheckboxSetting;

@SearchTags({"anti water push", "NoWaterPush", "no water push"})
public final class AntiWaterPushHack extends Hack implements UpdateListener,
	VelocityFromFluidListener, IsPlayerInWaterListener
{
	private final CheckboxSetting preventSlowdown = new CheckboxSetting(
		"Prevent slowdown", "Allows you to walk underwater at full speed.\n"
			+ "Some servers consider this a speedhack.",
		false);
	
	public AntiWaterPushHack()
	{
		super("AntiWaterPush");
		setCategory(Category.MOVEMENT);
		addSetting(preventSlowdown);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(VelocityFromFluidListener.class, this);
		EVENTS.add(IsPlayerInWaterListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(VelocityFromFluidListener.class, this);
		EVENTS.remove(IsPlayerInWaterListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(!preventSlowdown.isChecked())
			return;
		
		if(!MC.options.keyJump.isDown())
			return;
		
		if(!MC.player.onGround())
			return;
		
		if(!IMC.getPlayer().isTouchingWaterBypass())
			return;
		
		MC.player.jumpFromGround();
	}
	
	@Override
	public void onVelocityFromFluid(VelocityFromFluidEvent event)
	{
		if(event.getEntity() == MC.player)
			event.cancel();
	}
	
	@Override
	public void onIsPlayerInWater(IsPlayerInWaterEvent event)
	{
		if(preventSlowdown.isChecked())
			event.setInWater(false);
	}
	
	public boolean isPreventingSlowdown()
	{
		return preventSlowdown.isChecked();
	}
}
