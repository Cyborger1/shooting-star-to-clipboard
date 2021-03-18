package com.shootingstarstoclipboard;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ShootingStarsToClipboardPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ShootingStarsToClipboardPlugin.class);
		RuneLite.main(args);
	}
}