package com.shootingstarstoclipboard;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("shootingstarstoclipboard")
public interface ShootingStarsToClipboardConfig extends Config
{
	@ConfigItem(
		keyName = "copyFormat",
		name = "Copy Format",
		description = "Copy Format to use."
	)
	default CopyFormat copyFormat()
	{
		return CopyFormat.COMMA_DELIMITED;
	}
}
