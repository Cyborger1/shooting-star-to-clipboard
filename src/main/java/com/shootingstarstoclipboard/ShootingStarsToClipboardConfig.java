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
		description = "Copy Format to use.",
		position = 1
	)
	default CopyFormat copyFormat()
	{
		return CopyFormat.COMMA_DELIMITED;
	}

	@ConfigItem(
		keyName = "useUTCTimes",
		name = "Use UTC Times",
		description = "If true, ETA times are based on the UTC timezone, else use system time.",
		position = 2
	)
	default boolean useUTCTimes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "useShortTimes",
		name = "Use Short Times",
		description = "If true, ETA times are only hours/minutes, else also include year/month/day.",
		position = 3
	)
	default boolean useShortTimes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "timesToShow",
		name = "Times to Show",
		description = "Select the types of times to include in the copy text.",
		position = 4
	)
	default TimesToShow timesToShow()
	{
		return TimesToShow.ALL;
	}
}
