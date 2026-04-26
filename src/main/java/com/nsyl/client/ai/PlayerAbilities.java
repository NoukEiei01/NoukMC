/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.ai;

import net.minecraft.client.Minecraft;
import com.nsyl.client.NsylClient;
import com.nsyl.client.hack.HackList;

public record PlayerAbilities(boolean invulnerable, boolean creativeFlying,
	boolean flying, boolean immuneToFallDamage, boolean noWaterSlowdown,
	boolean jesus, boolean spider)
{
	
	private static final NsylClient CLIENT = NsylClient.INSTANCE;
	private static final Minecraft MC = NsylClient.MC;
	
	public static PlayerAbilities get()
	{
		HackList hax = NsylClient.INSTANCE.getHax();
		net.minecraft.world.entity.player.Abilities mcAbilities =
			MC.player.getAbilities();
		
		boolean invulnerable =
			mcAbilities.invulnerable || mcAbilities.instabuild;
		boolean creativeFlying = mcAbilities.flying;
		boolean flying = creativeFlying || hax.flightHack.isEnabled();
		boolean immuneToFallDamage = invulnerable || hax.noFallHack.isEnabled();
		boolean noWaterSlowdown = hax.antiWaterPushHack.isPreventingSlowdown();
		boolean jesus = hax.jesusHack.isEnabled();
		boolean spider = hax.spiderHack.isEnabled();
		
		return new PlayerAbilities(invulnerable, creativeFlying, flying,
			immuneToFallDamage, noWaterSlowdown, jesus, spider);
	}
}
