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
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.world.item.Items;
import com.nsyl.client.gametest.NsylTest;

public enum GiveCmdTest
{
	;
	
	public static void testGiveCmd(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		NsylTest.LOGGER.info("Testing .give command");
		
		runNsylCommand(context, "give diamond");
		clearToasts(context);
		context.waitTick();
		assertOneItemInSlot(context, 0, Items.DIAMOND);
		context.takeScreenshot("give_command_result");
		
		// Clean up
		clearInventory(context);
		clearChat(context);
		context.waitTicks(7);
	}
}
