/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks.portalesp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import com.nsyl.client.settings.CheckboxSetting;
import com.nsyl.client.settings.ColorSetting;
import com.nsyl.client.settings.Setting;
import com.nsyl.client.util.BlockUtils;

public final class PortalEspBlockGroup
{
	protected final ArrayList<AABB> boxes = new ArrayList<>();
	private final Block block;
	private final ColorSetting color;
	private final CheckboxSetting enabled;
	
	public PortalEspBlockGroup(Block block, ColorSetting color,
		CheckboxSetting enabled)
	{
		this.block = block;
		this.color = Objects.requireNonNull(color);
		this.enabled = enabled;
	}
	
	public void add(BlockPos pos)
	{
		AABB box = getBox(pos);
		if(box == null)
			return;
		
		boxes.add(box);
	}
	
	private AABB getBox(BlockPos pos)
	{
		if(!BlockUtils.canBeClicked(pos))
			return null;
		
		return BlockUtils.getBoundingBox(pos);
	}
	
	public void clear()
	{
		boxes.clear();
	}
	
	public boolean isEnabled()
	{
		return enabled == null || enabled.isChecked();
	}
	
	public Stream<Setting> getSettings()
	{
		return Stream.of(enabled, color).filter(Objects::nonNull);
	}
	
	public Block getBlock()
	{
		return block;
	}
	
	public int getColorI(int alpha)
	{
		return color.getColorI(alpha);
	}
	
	public List<AABB> getBoxes()
	{
		return Collections.unmodifiableList(boxes);
	}
	
}
