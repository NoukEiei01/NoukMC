/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.ai;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.phys.Vec3;
import com.nsyl.client.NsylClient;
import com.nsyl.client.util.BlockUtils;
import com.nsyl.client.util.RotationUtils;

public class WalkPathProcessor extends PathProcessor
{
	public WalkPathProcessor(ArrayList<PathPos> path)
	{
		super(path);
	}
	
	@Override
	public void process()
	{
		// get positions
		BlockPos pos;
		if(NsylClient.MC.player.onGround())
			pos = BlockPos.containing(NsylClient.MC.player.getX(),
				NsylClient.MC.player.getY() + 0.5,
				NsylClient.MC.player.getZ());
		else
			pos = BlockPos.containing(NsylClient.MC.player.position());
		PathPos nextPos = path.get(index);
		int posIndex = path.indexOf(pos);
		
		if(posIndex == -1)
			ticksOffPath++;
		else
			ticksOffPath = 0;
		
		// update index
		if(pos.equals(nextPos))
		{
			index++;
			
			// disable when done
			if(index >= path.size())
				done = true;
			return;
		}
		if(posIndex > index)
		{
			index = posIndex + 1;
			
			// disable when done
			if(index >= path.size())
				done = true;
			return;
		}
		
		lockControls();
		NsylClient.MC.player.getAbilities().flying = false;
		
		// face next position
		facePosition(nextPos);
		if(Mth.wrapDegrees(Math.abs(RotationUtils
			.getHorizontalAngleToLookVec(Vec3.atCenterOf(nextPos)))) > 90)
			return;
		
		if(NsylClient.INSTANCE.getHax().jesusHack.isEnabled())
		{
			// wait for Jesus to swim up
			if(NsylClient.MC.player.getY() < nextPos.getY()
				&& (NsylClient.MC.player.isInWater()
					|| NsylClient.MC.player.isInLava()))
				return;
			
			// manually swim down if using Jesus
			if(NsylClient.MC.player.getY() - nextPos.getY() > 0.5
				&& (NsylClient.MC.player.isInWater()
					|| NsylClient.MC.player.isInLava()
					|| NsylClient.INSTANCE.getHax().jesusHack.isOverLiquid()))
				MC.options.keyShift.setDown(true);
		}
		
		// horizontal movement
		if(pos.getX() != nextPos.getX() || pos.getZ() != nextPos.getZ())
		{
			MC.options.keyUp.setDown(true);
			
			if(index > 0 && path.get(index - 1).isJumping()
				|| pos.getY() < nextPos.getY())
				MC.options.keyJump.setDown(true);
			
			// vertical movement
		}else if(pos.getY() != nextPos.getY())
			// go up
			if(pos.getY() < nextPos.getY())
			{
				// climb up
				// TODO: Spider
				Block block = BlockUtils.getBlock(pos);
				if(block instanceof LadderBlock || block instanceof VineBlock)
				{
					NsylClient.INSTANCE.getRotationFaker().faceVectorClientIgnorePitch(
						BlockUtils.getBoundingBox(pos).getCenter());
					
					MC.options.keyUp.setDown(true);
					
				}else
				{
					// directional jump
					if(index < path.size() - 1
						&& !nextPos.above().equals(path.get(index + 1)))
						index++;
					
					// jump up
					MC.options.keyJump.setDown(true);
				}
				
				// go down
			}else
			{
				// skip mid-air nodes and go straight to the bottom
				while(index < path.size() - 1
					&& path.get(index).below().equals(path.get(index + 1)))
					index++;
				
				// walk off the edge
				if(NsylClient.MC.player.onGround())
					MC.options.keyUp.setDown(true);
			}
	}
	
	@Override
	public boolean canBreakBlocks()
	{
		return MC.player.onGround();
	}
}
