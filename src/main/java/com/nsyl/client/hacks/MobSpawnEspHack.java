/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import java.awt.Color;
import java.util.Map.Entry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.NsylRenderLayers;
import com.nsyl.client.events.PacketInputListener;
import com.nsyl.client.events.RenderListener;
import com.nsyl.client.events.UpdateListener;
import com.nsyl.client.hack.Hack;
import com.nsyl.client.hacks.mobspawnesp.HitboxCheckSetting;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.settings.ChunkAreaSetting;
import com.nsyl.client.settings.ChunkAreaSetting.ChunkArea;
import com.nsyl.client.settings.ColorSetting;
import com.nsyl.client.settings.SliderSetting;
import com.nsyl.client.settings.SliderSetting.ValueDisplay;
import com.nsyl.client.util.EasyVertexBuffer;
import com.nsyl.client.util.RegionPos;
import com.nsyl.client.util.RenderUtils;
import com.nsyl.client.util.chunk.ChunkSearcher;
import com.nsyl.client.util.chunk.ChunkSearcher.Result;
import com.nsyl.client.util.chunk.ChunkVertexBufferCoordinator;

@SearchTags({"mob spawn esp", "LightLevelESP", "light level esp",
	"LightLevelOverlay", "light level overlay"})
public final class MobSpawnEspHack extends Hack
	implements UpdateListener, RenderListener
{
	private final ChunkAreaSetting drawDistance =
		new ChunkAreaSetting("Draw distance", "", ChunkArea.A9);
	
	private final ColorSetting nightColor = new ColorSetting("Night color",
		"description.nsyl.setting.mobspawnesp.night_color", Color.YELLOW);
	
	private final ColorSetting dayColor = new ColorSetting("Day color",
		"description.nsyl.setting.mobspawnesp.day_color", Color.RED);
	
	private final SliderSetting opacity =
		new SliderSetting("Opacity", 0.5, 0, 1, 0.01, ValueDisplay.PERCENTAGE);
	
	private final CheckboxSetting depthTest =
		new CheckboxSetting("Depth test", true);
	
	private final HitboxCheckSetting hitboxCheck = new HitboxCheckSetting();
	
	private final ChunkVertexBufferCoordinator coordinator =
		new ChunkVertexBufferCoordinator(this::isSpawnable, Mode.LINES,
			DefaultVertexFormat.POSITION_COLOR_NORMAL, this::buildBuffer,
			drawDistance);
	
	private int cachedDayColor;
	private int cachedNightColor;
	
	public MobSpawnEspHack()
	{
		super("MobSpawnESP");
		setCategory(Category.RENDER);
		addSetting(drawDistance);
		addSetting(nightColor);
		addSetting(dayColor);
		addSetting(opacity);
		addSetting(depthTest);
		addSetting(hitboxCheck);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(PacketInputListener.class, coordinator);
		EVENTS.add(RenderListener.class, this);
		
		cachedDayColor = dayColor.getColorI();
		cachedNightColor = nightColor.getColorI();
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(PacketInputListener.class, coordinator);
		EVENTS.remove(RenderListener.class, this);
		
		coordinator.reset();
	}
	
	@Override
	public void onUpdate()
	{
		if(dayColor.getColorI() != cachedDayColor
			|| nightColor.getColorI() != cachedNightColor)
		{
			cachedDayColor = dayColor.getColorI();
			cachedNightColor = nightColor.getColorI();
			coordinator.reset();
		}
		
		coordinator.update();
	}
	
	@Override
	public void onRender(PoseStack matrixStack, float partialTicks)
	{
		RenderSystem.setShaderColor(1, 1, 1, opacity.getValueF());
		RenderType layer = NsylRenderLayers.getLines(depthTest.isChecked());
		
		for(Entry<ChunkPos, EasyVertexBuffer> entry : coordinator.getBuffers())
		{
			RegionPos region = RegionPos.of(entry.getKey());
			
			matrixStack.pushPose();
			RenderUtils.applyRegionalRenderOffset(matrixStack, region);
			
			entry.getValue().draw(matrixStack, layer);
			
			matrixStack.popPose();
		}
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
	
	private boolean isSpawnable(BlockPos pos, BlockState state)
	{
		// Check for solid blocks, fluids, redstone, prevent_spawning tags, etc.
		// See SpawnLocationTypes.ON_GROUND
		if(!SpawnPlacements.isSpawnPositionOk(EntityType.CREEPER, MC.level,
			pos))
			return false;
		
		// Check for hitbox collisions
		if(!hitboxCheck.isSpaceEmpty(pos))
			return false;
		
		// Check block light level
		return MC.level.getBrightness(LightLayer.BLOCK, pos) < 1;
	}
	
	private void buildBuffer(VertexConsumer buffer, ChunkSearcher searcher,
		Iterable<Result> results)
	{
		RegionPos region = RegionPos.of(searcher.getPos());
		
		for(Result result : results)
		{
			if(searcher.isInterrupted())
				return;
			
			drawCross(buffer, result.pos(), region);
		}
	}
	
	private void drawCross(VertexConsumer buffer, BlockPos pos,
		RegionPos region)
	{
		float x1 = pos.getX() - region.x();
		float x2 = x1 + 1;
		float y = pos.getY() + 0.01F;
		float z1 = pos.getZ() - region.z();
		float z2 = z1 + 1;
		
		int color = MC.level.getBrightness(LightLayer.SKY, pos) < 8
			? cachedDayColor : cachedNightColor;
		
		buffer.addVertex(x1, y, z1).setColor(color).setNormal(1, 0, 1);
		buffer.addVertex(x2, y, z2).setColor(color).setNormal(1, 0, 1);
		buffer.addVertex(x2, y, z1).setColor(color).setNormal(-1, 0, 1);
		buffer.addVertex(x1, y, z2).setColor(color).setNormal(-1, 0, 1);
	}
}
