/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.world.item.Items;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.settings.CheckboxSetting;

@SearchTags({"no fall"})
public final class NoFallHack extends Hack implements UpdateListener
{
	private final CheckboxSetting allowElytra = new CheckboxSetting(
		"Allow elytra", "description.nsyl.setting.nofall.allow_elytra", false);
	
	private final CheckboxSetting pauseForMace =
		new CheckboxSetting("Pause for mace",
			"description.nsyl.setting.nofall.pause_for_mace", false);
	
	public NoFallHack()
	{
		super("NoFall");
		setCategory(Category.MOVEMENT);
		addSetting(allowElytra);
		addSetting(pauseForMace);
	}
	
	@Override
	public String getRenderName()
	{
		LocalPlayer player = MC.player;
		if(player == null)
			return getName();
		
		if(player.isFallFlying() && !allowElytra.isChecked())
			return getName() + " (paused)";
		
		if(player.isCreative())
			return getName() + " (paused)";
		
		if(pauseForMace.isChecked() && isHoldingMace(player))
			return getName() + " (paused)";
		
		return getName();
	}
	
	@Override
	protected void onEnable()
	{
		CLIENT.getHax().antiHungerHack.setEnabled(false);
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
		// do nothing in creative mode, since there is no fall damage anyway
		LocalPlayer player = MC.player;
		if(player.isCreative())
			return;
		
		// pause when flying with elytra, unless allowed
		boolean fallFlying = player.isFallFlying();
		if(fallFlying && !allowElytra.isChecked())
			return;
		
		// pause when holding a mace, if enabled
		if(pauseForMace.isChecked() && isHoldingMace(player))
			return;
		
		// attempt to fix elytra weirdness, if allowed
		if(fallFlying && player.isShiftKeyDown()
			&& !isFallingFastEnoughToCauseDamage(player))
			return;
		
		// send packet to stop fall damage
		player.connection
			.send(new StatusOnly(true, MC.player.horizontalCollision));
	}
	
	private boolean isHoldingMace(LocalPlayer player)
	{
		return player.getMainHandItem().is(Items.MACE);
	}
	
	private boolean isFallingFastEnoughToCauseDamage(LocalPlayer player)
	{
		return player.getDeltaMovement().y < -0.5;
	}
}
