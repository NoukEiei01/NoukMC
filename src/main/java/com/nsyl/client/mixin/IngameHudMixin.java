/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import com.nsyl.client.NsylClient;
import com.nsyl.client.event.EventManager;
import com.nsyl.client.events.GUIRenderListener.GUIRenderEvent;
import com.nsyl.client.hack.HackList;

@Mixin(Gui.class)
public class IngameHudMixin
{
	@Shadow
	@Final
	private DebugScreenOverlay debugOverlay;
	
	// runs after renderScoreboardSidebar()
	// and before playerListHud.setVisible()
	@Inject(at = @At("HEAD"),
		method = "renderTabList(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V")
	private void onRenderPlayerList(GuiGraphics context,
		DeltaTracker tickCounter, CallbackInfo ci)
	{
		if(debugOverlay.showDebugScreen())
			return;
		
		float tickDelta = tickCounter.getGameTimeDeltaPartialTick(true);
		EventManager.fire(new GUIRenderEvent(context, tickDelta));
	}
	
	@Inject(at = @At("HEAD"),
		method = "renderTextureOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;F)V",
		cancellable = true)
	private void onRenderOverlay(GuiGraphics context, ResourceLocation texture,
		float opacity, CallbackInfo ci)
	{
		if(texture == null)
			return;
		
		String path = texture.getPath();
		HackList hax = NsylClient.INSTANCE.getHax();
		
		if("textures/misc/pumpkinblur.png".equals(path)
			&& hax.noPumpkinHack.isEnabled())
			ci.cancel();
		
		if("textures/misc/powder_snow_outline.png".equals(path)
			&& hax.noOverlayHack.isEnabled())
			ci.cancel();
	}
	
	@Inject(at = @At("HEAD"), method = "renderVignette", cancellable = true)
	private void onRenderVignetteOverlay(GuiGraphics context, Entity entity,
		CallbackInfo ci)
	{
		HackList hax = NsylClient.INSTANCE.getHax();
		if(hax == null || !hax.noVignetteHack.isEnabled())
			return;
		
		ci.cancel();
	}
}
