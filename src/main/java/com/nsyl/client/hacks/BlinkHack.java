/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import java.util.ArrayDeque;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.events.PacketOutputListener;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.hack.DontSaveState;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.settings.SliderSetting;
import com.nsyl.client.settings.SliderSetting.ValueDisplay;
import com.nsyl.client.util.FakePlayerEntity;

@DontSaveState
@SearchTags({"LagSwitch", "lag switch"})
public final class BlinkHack extends Hack
	implements UpdateListener, PacketOutputListener
{
	private final SliderSetting limit = new SliderSetting("Limit",
		"Automatically restarts Blink once the given number of packets have been suspended.\n\n"
			+ "0 = no limit",
		0, 0, 500, 1, ValueDisplay.INTEGER.withLabel(0, "disabled"));
	
	private final ArrayDeque<ServerboundMovePlayerPacket> packets =
		new ArrayDeque<>();
	private FakePlayerEntity fakePlayer;
	
	public BlinkHack()
	{
		super("Blink");
		setCategory(Category.MOVEMENT);
		addSetting(limit);
	}
	
	@Override
	public String getRenderName()
	{
		if(limit.getValueI() == 0)
			return getName() + " [" + packets.size() + "]";
		return getName() + " [" + packets.size() + "/" + limit.getValueI()
			+ "]";
	}
	
	@Override
	protected void onEnable()
	{
		fakePlayer = new FakePlayerEntity();
		
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(PacketOutputListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(PacketOutputListener.class, this);
		
		fakePlayer.despawn();
		packets.forEach(p -> MC.player.connection.send(p));
		packets.clear();
	}
	
	@Override
	public void onUpdate()
	{
		if(limit.getValueI() == 0)
			return;
		
		if(packets.size() >= limit.getValueI())
		{
			setEnabled(false);
			setEnabled(true);
		}
	}
	
	@Override
	public void onSentPacket(PacketOutputEvent event)
	{
		if(!(event.getPacket() instanceof ServerboundMovePlayerPacket))
			return;
		
		event.cancel();
		
		ServerboundMovePlayerPacket packet =
			(ServerboundMovePlayerPacket)event.getPacket();
		ServerboundMovePlayerPacket prevPacket = packets.peekLast();
		
		if(prevPacket != null && packet.isOnGround() == prevPacket.isOnGround()
			&& packet.getYRot(-1) == prevPacket.getYRot(-1)
			&& packet.getXRot(-1) == prevPacket.getXRot(-1)
			&& packet.getX(-1) == prevPacket.getX(-1)
			&& packet.getY(-1) == prevPacket.getY(-1)
			&& packet.getZ(-1) == prevPacket.getZ(-1))
			return;
		
		packets.addLast(packet);
	}
	
	public void cancel()
	{
		packets.clear();
		fakePlayer.resetPlayerPosition();
		setEnabled(false);
	}
}
