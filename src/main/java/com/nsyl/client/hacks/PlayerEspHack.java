/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.events.CameraTransformViewBobbingListener;
import com.nsyl.client.events.RenderListener;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.settings.EspBoxSizeSetting;
import com.nsyl.client.settings.EspStyleSetting;
import com.nsyl.client.settings.EspStyleSetting.EspStyle;
import com.nsyl.client.settings.filterlists.EntityFilterList;
import com.nsyl.client.settings.filters.FilterInvisibleSetting;
import com.nsyl.client.settings.filters.FilterSleepingSetting;
import com.nsyl.client.util.EntityUtils;
import com.nsyl.client.util.FakePlayerEntity;
import com.nsyl.client.util.RenderUtils;
import com.nsyl.client.util.RenderUtils.ColoredBox;
import com.nsyl.client.util.RenderUtils.ColoredPoint;

@SearchTags({"player esp", "PlayerTracers", "player tracers"})
public final class PlayerEspHack extends Hack implements UpdateListener,
	CameraTransformViewBobbingListener, RenderListener
{
	private final EspStyleSetting style =
		new EspStyleSetting(EspStyle.LINES_AND_BOXES);
	
	private final EspBoxSizeSetting boxSize = new EspBoxSizeSetting(
		"\u00a7lAccurate\u00a7r mode shows the exact hitbox of each player.\n"
			+ "\u00a7lFancy\u00a7r mode shows slightly larger boxes that look better.");
	
	private final EntityFilterList entityFilters = new EntityFilterList(
		new FilterSleepingSetting("Won't show sleeping players.", false),
		new FilterInvisibleSetting("Won't show invisible players.", false));
	
	// --- Performance: pre-built render lists, rebuilt in onUpdate not onRender ---
	private final ArrayList<Player> players = new ArrayList<>();
	private final ArrayList<ColoredBox> boxes = new ArrayList<>();
	private final ArrayList<ColoredPoint> ends = new ArrayList<>();
	private final java.util.HashMap<Player, Integer> colorCache =
		new java.util.HashMap<>();
	
	// Track style/boxSize so we only rebuild render lists when needed
	private EspStyle lastStyle;
	private double lastExtraSize;
	
	public PlayerEspHack()
	{
		super("PlayerESP");
		setCategory(Category.RENDER);
		addSetting(style);
		addSetting(boxSize);
		entityFilters.forEach(this::addSetting);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(CameraTransformViewBobbingListener.class, this);
		EVENTS.add(RenderListener.class, this);
		lastStyle = null; // force rebuild on next update
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(CameraTransformViewBobbingListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		players.clear();
		boxes.clear();
		ends.clear();
		colorCache.clear();
	}
	
	@Override
	public void onUpdate()
	{
		players.clear();
		colorCache.clear();
		
		// Use sequential stream — avoids ForkJoinPool overhead for small lists
		Stream<AbstractClientPlayer> stream = MC.level.players().stream()
			.filter(e -> !e.isRemoved() && e.getHealth() > 0)
			.filter(e -> e != MC.player)
			.filter(e -> !(e instanceof FakePlayerEntity))
			.filter(e -> Math.abs(e.getY() - MC.player.getY()) <= 1e6);
		
		stream = entityFilters.applyTo(stream);
		
		stream.collect(Collectors.toList()).forEach(e -> {
			players.add(e);
			colorCache.put(e, computeColor(e));
		});
		
		// Pre-build render lists here (once per tick, not once per frame)
		EspStyle currentStyle = style.getSelected();
		double currentExtraSize = boxSize.getExtraSize() / 2;
		
		boxes.clear();
		ends.clear();
		
		for(Player e : players)
		{
			AABB box = e.getBoundingBox()
				.move(0, currentExtraSize, 0).inflate(currentExtraSize);
			
			if(currentStyle != EspStyle.LINES)
				boxes.add(new ColoredBox(box, getColor(e)));
			
			if(currentStyle != EspStyle.BOXES)
				ends.add(new ColoredPoint(box.getCenter(), getColor(e)));
		}
		
		lastStyle = currentStyle;
		lastExtraSize = currentExtraSize;
	}
	
	@Override
	public void onCameraTransformViewBobbing(
		CameraTransformViewBobbingEvent event)
	{
		if(style.hasLines())
			event.cancel();
	}
	
	@Override
	public void onRender(PoseStack matrixStack, float partialTicks)
	{
		// Render pre-built lists — no allocation on the render thread
		if(style.hasBoxes() && !boxes.isEmpty())
			RenderUtils.drawOutlinedBoxes(matrixStack, boxes, false);
		
		if(style.hasLines() && !ends.isEmpty())
			RenderUtils.drawTracers(matrixStack, partialTicks, ends, false);
	}
	
	private int getColor(Player e)
	{
		return colorCache.getOrDefault(e, 0x8000FF00);
	}
	
	private int computeColor(Player e)
	{
		if(CLIENT.getFriends().contains(e.getName().getString()))
			return 0x800000FF;
		
		float f = MC.player.distanceTo(e) / 20F;
		float r = Mth.clamp(2 - f, 0, 1);
		float g = Mth.clamp(f, 0, 1);
		float[] rgb = {r, g, 0};
		return RenderUtils.toIntColor(rgb, 0.5F);
	}
}
