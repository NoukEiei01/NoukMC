/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.nsyl.client.NsylClient;
import com.nsyl.client.event.EventManager;
import com.nsyl.client.events.DeathListener.DeathEvent;
import com.nsyl.client.hacks.AutoRespawnHack;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen
{
	private DeathScreenMixin(NsylClient nsyl, Component title)
	{
		super(title);
	}
	
	@Inject(at = @At("TAIL"), method = "tick()V")
	private void onTick(CallbackInfo ci)
	{
		EventManager.fire(DeathEvent.INSTANCE);
	}
	
	@Inject(at = @At("TAIL"), method = "init()V")
	private void onInit(CallbackInfo ci)
	{
		AutoRespawnHack autoRespawn =
			NsylClient.INSTANCE.getHax().autoRespawnHack;
		
		if(!autoRespawn.shouldShowButton())
			return;
		
		int backButtonX = width / 2 - 100;
		int backButtonY = height / 4;
		
		addRenderableWidget(
			Button.builder(Component.literal("AutoRespawn: OFF"), b -> {
				autoRespawn.setEnabled(true);
				autoRespawn.onDeath();
			}).bounds(backButtonX, backButtonY + 48, 200, 20).build());
	}
}
