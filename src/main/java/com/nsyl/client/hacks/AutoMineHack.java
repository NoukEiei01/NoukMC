/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.events.HandleBlockBreakingListener;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.mixinterface.IKeyBinding;
import com.nsyl.client.settings.CheckboxSetting;

@SearchTags({"auto mine", "AutoBreak", "auto break"})
public final class AutoMineHack extends Hack
	implements UpdateListener, HandleBlockBreakingListener
{
	private final CheckboxSetting superFastMode =
		new CheckboxSetting("Super fast mode",
			"Breaks blocks faster than you normally could. May get detected by"
				+ " anti-cheat plugins.",
			false);
	
	public AutoMineHack()
	{
		super("AutoMine");
		setCategory(Category.BLOCKS);
		addSetting(superFastMode);
	}
	
	@Override
	protected void onEnable()
	{
		CLIENT.getHax().autoFarmHack.setEnabled(false);
		CLIENT.getHax().excavatorHack.setEnabled(false);
		CLIENT.getHax().nukerHack.setEnabled(false);
		CLIENT.getHax().nukerLegitHack.setEnabled(false);
		CLIENT.getHax().speedNukerHack.setEnabled(false);
		CLIENT.getHax().tunnellerHack.setEnabled(false);
		CLIENT.getHax().veinMinerHack.setEnabled(false);
		
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(HandleBlockBreakingListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(HandleBlockBreakingListener.class, this);
		IKeyBinding.get(MC.options.keyAttack).resetPressedState();
		MC.gameMode.stopDestroyBlock();
	}
	
	@Override
	public void onUpdate()
	{
		MultiPlayerGameMode im = MC.gameMode;
		
		// Ignore the attack cooldown because opening any screen
		// will set it to 10k ticks.
		
		if(MC.player.isHandsBusy())
		{
			im.stopDestroyBlock();
			return;
		}
		
		HitResult hitResult = MC.hitResult;
		if(hitResult == null || hitResult.getType() != HitResult.Type.BLOCK
			|| !(hitResult instanceof BlockHitResult bHitResult))
		{
			im.stopDestroyBlock();
			return;
		}
		
		BlockPos pos = bHitResult.getBlockPos();
		BlockState state = MC.level.getBlockState(pos);
		Direction side = bHitResult.getDirection();
		if(state.isAir())
		{
			im.stopDestroyBlock();
			return;
		}
		
		CLIENT.getHax().autoToolHack.equipIfEnabled(pos);
		
		if(MC.player.isUsingItem())
			// This case doesn't cancel block breaking in vanilla Minecraft.
			return;
		
		if(!im.isDestroying())
			im.startDestroyBlock(pos, side);
		
		if(im.continueDestroyBlock(pos, side))
		{
			MC.particleEngine.crack(pos, side);
			MC.player.swing(InteractionHand.MAIN_HAND);
			MC.options.keyAttack.setDown(true);
		}
	}
	
	@Override
	public void onHandleBlockBreaking(HandleBlockBreakingEvent event)
	{
		// Cancel vanilla block breaking so we don't send the packets twice.
		if(!superFastMode.isChecked())
			event.cancel();
	}
}
