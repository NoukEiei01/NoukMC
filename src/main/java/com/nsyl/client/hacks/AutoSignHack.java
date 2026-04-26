/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.hacks;

import com.nsyl.client.Category;
import com.nsyl.client.SearchTags;
import com.nsyl.client.hack.DontSaveState;
import com.nsyl.client.hack.Hack;

@SearchTags({"auto sign"})
@DontSaveState
public final class AutoSignHack extends Hack
{
	private String[] signText;
	
	public AutoSignHack()
	{
		super("AutoSign");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	protected void onDisable()
	{
		signText = null;
	}
	
	public String[] getSignText()
	{
		return signText;
	}
	
	public void setSignText(String[] signText)
	{
		if(isEnabled() && this.signText == null)
			this.signText = signText;
	}
}
