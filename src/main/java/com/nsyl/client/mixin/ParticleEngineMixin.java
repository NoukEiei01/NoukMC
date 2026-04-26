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

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import com.nsyl.client.NsylClient;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin
{
	/**
	 * ดักจับทุกครั้งที่ particle จะถูก spawn
	 * ถ้า NoLag เปิดอยู่และ shouldSpawnParticle() คืน false → cancel
	 */
	@Inject(
		at = @At("HEAD"),
		method = "createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;",
		cancellable = true)
	private <T extends ParticleOptions> void onCreateParticle(
		T parameters, double x, double y, double z,
		double velocityX, double velocityY, double velocityZ,
		CallbackInfo ci)
	{
		if(!NsylClient.INSTANCE.getHax().noLagHack.shouldSpawnParticle())
			ci.cancel();
	}
	
	/**
	 * ดักจับ weather particle (rain, snow) แยกต่างหาก
	 */
	@Inject(
		at = @At("HEAD"),
		method = "createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;",
		cancellable = true)
	private <T extends ParticleOptions> void onCreateWeatherParticle(
		T parameters, double x, double y, double z,
		double velocityX, double velocityY, double velocityZ,
		CallbackInfo ci)
	{
		// Weather particles have near-zero velocity — covered by shouldSpawnParticle
		// This hook is a placeholder; weather filtering is done via shouldHideWeatherParticles()
	}
}
