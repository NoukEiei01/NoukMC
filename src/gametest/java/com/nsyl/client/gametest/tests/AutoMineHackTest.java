/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.gametest.tests;

import static com.nsyl.client.gametest.NsylClientTestHelper.*;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestClientWorldContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.world.level.block.Blocks;
import com.nsyl.client.gametest.NsylTest;

public enum AutoMineHackTest
{
	;
	
	public static void testAutoMineHack(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		NsylTest.LOGGER.info("Testing AutoMine hack");
		TestClientWorldContext world = spContext.getClientWorld();
		TestServerContext server = spContext.getServer();
		runCommand(server, "gamemode survival");
		
		// Break a dirt block in survival mode
		runCommand(server, "setblock ~ ~1 ~2 minecraft:dirt");
		waitForBlock(context, 0, 1, 2, Blocks.DIRT);
		runNsylCommand(context, "t AutoMine on");
		waitForBlock(context, 0, 1, 2, Blocks.AIR);
		context.waitTick();
		world.waitForChunksRender();
		context.takeScreenshot("automine_survival");
		
		// Clean up
		runNsylCommand(context, "t AutoMine off");
		runCommand(server, "gamemode creative");
		runCommand(server, "kill @e[type=item]");
		clearInventory(context);
		context.waitTick();
		clearParticles(context);
		clearChat(context);
		context.waitTick();
	}
}
