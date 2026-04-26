/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Inventory;
import com.nsyl.client.NsylClient;
import com.nsyl.client.event.EventManager;
import com.nsyl.client.events.MouseScrollListener.MouseScrollEvent;
import com.nsyl.client.events.MouseUpdateListener.MouseUpdateEvent;

@Mixin(MouseHandler.class)
public class MouseMixin
{
	@Shadow
	private double accumulatedDX;
	@Shadow
	private double accumulatedDY;
	
	@Inject(at = @At("RETURN"), method = "onScroll(JDD)V")
	private void onOnMouseScroll(long window, double horizontal,
		double vertical, CallbackInfo ci)
	{
		EventManager.fire(new MouseScrollEvent(vertical));
	}
	
	@Inject(at = @At("HEAD"), method = "handleAccumulatedMovement()V")
	private void onTick(CallbackInfo ci)
	{
		MouseUpdateEvent event =
			new MouseUpdateEvent(accumulatedDX, accumulatedDY);
		EventManager.fire(event);
		accumulatedDX = event.getDeltaX();
		accumulatedDY = event.getDeltaY();
	}
	
	@WrapWithCondition(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedHotbarSlot(I)V"),
		method = "onScroll(JDD)V")
	private boolean wrapOnMouseScroll(Inventory inventory, int slot)
	{
		return !NsylClient.INSTANCE.getOtfs().zoomOtf
			.shouldPreventHotbarScrolling();
	}
}
