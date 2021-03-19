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
		description = "Copy Format to use. For delimited types, data is in the following order by default:<br>" +
			"World, Location(s), Estimate From, Estimate To, ETA (Average of the two estimates)",
		position = 1
	)
	default CopyFormat copyFormat()
	{
		return CopyFormat.COMMA_DELIMITED;
	}

	@ConfigItem(
		keyName = "showBothLocations",
		name = "Show Both Locations",
		description = "If true, both locations for double location areas are added to the copy text, else only keep the first location.",
		position = 2
	)
	default boolean showBothLocations()
	{
		return true;
	}

	@ConfigItem(
		keyName = "useUTCTimes",
		name = "Use UTC Times",
		description = "If true, ETA times are based on the UTC timezone, else use system time.",
		position = 3
	)
	default boolean useUTCTimes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "useShortTimes",
		name = "Use Short Times",
		description = "If true, ETA times are only hours/minutes, else also include year/month/day.",
		position = 4
	)
	default boolean useShortTimes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "timesToShow",
		name = "Times to Show",
		description = "Select the types of times to include in the copy text.",
		position = 5
	)
	default TimesToShow timesToShow()
	{
		return TimesToShow.ALL;
	}

	@ConfigItem(
		keyName = "showPVPIdentifier",
		name = "Show PVP Identifier",
		description = "Adds '(pvp)' after the world number when appropriate.",
		position = 6
	)
	default boolean showPVPIdentifier()
	{
		return true;
	}

	@ConfigItem(
		keyName = "appendNewLine",
		name = "Append NewLine",
		description = "Adds a newline return at the end of the copied text.",
		position = 7
	)
	default boolean appendNewLine()
	{
		return true;
	}
}
