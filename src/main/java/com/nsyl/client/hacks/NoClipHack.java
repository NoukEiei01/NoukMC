/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import net.minecraft.client.player.LocalPlayer;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.events.AirStrafingSpeedListener;
import com.nsyl.client.events.IsNormalCubeListener;
import com.nsyl.client.events.PlayerMoveListener;
import com.nsyl.client.events.SetOpaqueCubeListener;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.hack.Hack;

@SearchTags({"no clip"})
public final class NoClipHack extends Hack
	implements UpdateListener, PlayerMoveListener, IsNormalCubeListener,
	SetOpaqueCubeListener, AirStrafingSpeedListener
{
	public NoClipHack()
	{
		super("NoClip");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(PlayerMoveListener.class, this);
		EVENTS.add(IsNormalCubeListener.class, this);
		EVENTS.add(SetOpaqueCubeListener.class, this);
		EVENTS.add(AirStrafingSpeedListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(PlayerMoveListener.class, this);
		EVENTS.remove(IsNormalCubeListener.class, this);
		EVENTS.remove(SetOpaqueCubeListener.class, this);
		EVENTS.remove(AirStrafingSpeedListener.class, this);
		
		MC.player.noPhysics = false;
	}
	
	@Override
	public void onUpdate()
	{
		LocalPlayer player = MC.player;
		
		player.noPhysics = true;
		player.fallDistance = 0;
		player.setOnGround(false);
		
		player.getAbilities().flying = false;
		player.setDeltaMovement(0, 0, 0);
		
		float speed = 0.2F;
		if(MC.options.keyJump.isDown())
			player.push(0, speed, 0);
		if(MC.options.keyShift.isDown())
			player.push(0, -speed, 0);
	}
	
	@Override
	public void onGetAirStrafingSpeed(AirStrafingSpeedEvent event)
	{
		event.setSpeed(0.2F);
	}
	
	@Override
	public void onPlayerMove()
	{
		MC.player.noPhysics = true;
	}
	
	@Override
	public void onIsNormalCube(IsNormalCubeEvent event)
	{
		event.cancel();
	}
	
	@Override
	public void onSetOpaqueCube(SetOpaqueCubeEvent event)
	{
		event.cancel();
	}
}
