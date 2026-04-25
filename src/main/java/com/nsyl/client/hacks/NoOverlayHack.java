/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.hack.Hack;

@SearchTags({"no overlay", "NoWaterOverlay", "no water overlay",
	"NoSnowOverlay", "no snow overlay"})
public final class NoOverlayHack extends Hack
{
	public NoOverlayHack()
	{
		super("NoOverlay");
		setCategory(Category.RENDER);
	}
	
	// See CameraMixin.onGetSubmersionType(), IngameHudMixin.renderOverlay(),
	// InGameOverlayRendererMixin.onRenderUnderwaterOverlay()
}
