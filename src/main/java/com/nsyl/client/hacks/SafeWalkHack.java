/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.AABB;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.mixinterface.IKeyBinding;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.settings.SliderSetting;
import com.nsyl.client.settings.SliderSetting.ValueDisplay;

@SearchTags({"safe walk", "SneakSafety", "sneak safety", "SpeedBridgeHelper",
	"speed bridge helper"})
public final class SafeWalkHack extends Hack
{
	private final CheckboxSetting sneak =
		new CheckboxSetting("Sneak at edges", "Visibly sneak at edges.", false);
	
	private final SliderSetting edgeDistance = new SliderSetting(
		"Sneak edge distance",
		"How close SafeWalk will let you get to the edge before sneaking.\n\n"
			+ "This setting is only used when \"Sneak at edges\" is enabled.",
		0.05, 0.05, 0.25, 0.001, ValueDisplay.DECIMAL.withSuffix("m"));
	
	private boolean sneaking;
	
	public SafeWalkHack()
	{
		super("SafeWalk");
		setCategory(Category.MOVEMENT);
		addSetting(sneak);
		addSetting(edgeDistance);
	}
	
	@Override
	protected void onEnable()
	{
		WURST.getHax().parkourHack.setEnabled(false);
		sneaking = false;
	}
	
	@Override
	protected void onDisable()
	{
		if(sneaking)
			setSneaking(false);
	}
	
	public void onClipAtLedge(boolean clipping)
	{
		LocalPlayer player = MC.player;
		
		if(!isEnabled() || !sneak.isChecked() || !player.onGround())
		{
			if(sneaking)
				setSneaking(false);
			
			return;
		}
		
		AABB box = player.getBoundingBox();
		AABB adjustedBox = box.expandTowards(0, -player.maxUpStep(), 0)
			.inflate(-edgeDistance.getValue(), 0, -edgeDistance.getValue());
		
		if(MC.level.noCollision(player, adjustedBox))
			clipping = true;
		
		setSneaking(clipping);
	}
	
	private void setSneaking(boolean sneaking)
	{
		IKeyBinding sneakKey = IKeyBinding.get(MC.options.keyShift);
		
		if(sneaking)
			sneakKey.setDown(true);
		else
			sneakKey.resetPressedState();
		
		this.sneaking = sneaking;
	}
	
	// See ClientPlayerEntityMixin
}
