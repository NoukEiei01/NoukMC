/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.nsyl.client.Category;
import com.nsyl.client.events.CameraTransformViewBobbingListener;
import com.nsyl.client.events.RenderListener;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.hacks.chestesp.ChestEspGroup;
import com.nsyl.client.hacks.chestesp.ChestEspGroupManager;
import com.nsyl.client.settings.EspStyleSetting;
import com.nsyl.client.util.RenderUtils;
import com.nsyl.client.util.chunk.ChunkUtils;

public class ChestEspHack extends Hack implements UpdateListener,
	CameraTransformViewBobbingListener, RenderListener
{
	private final EspStyleSetting style = new EspStyleSetting();
	private final ChestEspGroupManager groups = new ChestEspGroupManager();
	private int scanCooldown = 0;
	
	public ChestEspHack()
	{
		super("ChestESP");
		setCategory(Category.RENDER);
		addSetting(style);
		groups.allGroups.stream().flatMap(ChestEspGroup::getSettings)
			.forEach(this::addSetting);
	}
	
	@Override
	protected void onEnable()
	{
		scanCooldown = 0;
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(CameraTransformViewBobbingListener.class, this);
		EVENTS.add(RenderListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(CameraTransformViewBobbingListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		groups.allGroups.forEach(ChestEspGroup::clear);
	}
	
	@Override
	public void onUpdate()
	{
		if(scanCooldown-- > 0)
			return;
		scanCooldown = 5; // สแกนทุก 5 tick (~250ms) แทนทุก tick
		
		groups.allGroups.forEach(ChestEspGroup::clear);
		ChunkUtils.getLoadedBlockEntities().forEach(
			be -> groups.blockGroups.forEach(group -> group.addIfMatches(be)));
		MC.level.entitiesForRendering().forEach(
			e -> groups.entityGroups.forEach(group -> group.addIfMatches(e)));
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
		groups.entityGroups.stream().filter(ChestEspGroup::isEnabled)
			.forEach(g -> g.updateBoxes(partialTicks));
		
		if(style.hasBoxes())
			renderBoxes(matrixStack);
		
		if(style.hasLines())
			renderTracers(matrixStack, partialTicks);
	}
	
	private void renderBoxes(PoseStack matrixStack)
	{
		for(ChestEspGroup group : groups.allGroups)
		{
			if(!group.isEnabled())
				continue;
			
			List<AABB> boxes = group.getBoxes();
			int quadsColor = group.getColorI(0x40);
			int linesColor = group.getColorI(0x80);
			
			RenderUtils.drawSolidBoxes(matrixStack, boxes, quadsColor, false);
			RenderUtils.drawOutlinedBoxes(matrixStack, boxes, linesColor,
				false);
		}
	}
	
	private void renderTracers(PoseStack matrixStack, float partialTicks)
	{
		for(ChestEspGroup group : groups.allGroups)
		{
			if(!group.isEnabled())
				continue;
			
			List<AABB> boxes = group.getBoxes();
			List<Vec3> ends = boxes.stream().map(AABB::getCenter).toList();
			int color = group.getColorI(0x80);
			
			RenderUtils.drawTracers(matrixStack, partialTicks, ends, color,
				false);
		}
	}
}
