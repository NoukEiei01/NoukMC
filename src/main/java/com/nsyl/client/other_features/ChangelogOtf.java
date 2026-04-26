/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.other_features;

import net.minecraft.Util;
import com.nsyl.client.DontBlock;
import com.nsyl.client.SearchTags;
import com.nsyl.client.NsylClient;
import com.nsyl.client.other_feature.OtherFeature;
import com.nsyl.client.update.Version;

@SearchTags({"change log", "nsyl update", "release notes", "what's new",
	"what is new", "new features", "recently added features"})
@DontBlock
public final class ChangelogOtf extends OtherFeature
{
	public ChangelogOtf()
	{
		super("Changelog", "Opens the changelog in your browser.");
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "View Changelog";
	}
	
	@Override
	public void doPrimaryAction()
	{
		String link = new Version(NsylClient.VERSION).getChangelogLink()
			+ "?utm_source=NSYL+Client&utm_medium=ChangelogOtf&utm_content=View+Changelog";
		Util.getPlatform().openUri(link);
	}
}
