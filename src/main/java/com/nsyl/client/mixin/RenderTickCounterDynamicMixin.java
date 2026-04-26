/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.DeltaTracker;
import com.nsyl.client.NsylClient;

@Mixin(DeltaTracker.Timer.class)
public abstract class RenderTickCounterDynamicMixin
{
	@Shadow
	public float deltaTicks;
	
	@Inject(
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/DeltaTracker$Timer;lastMs:J",
			opcode = Opcodes.PUTFIELD,
			ordinal = 0),
		method = "advanceGameTime(J)I")
	public void onBeginRenderTick(long timeMillis,
		CallbackInfoReturnable<Integer> cir)
	{
		deltaTicks *= NsylClient.INSTANCE.getHax().timerHack.getTimerSpeed();
	}
}
