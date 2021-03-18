package com.shootingstarstoclipboard;

import com.google.inject.Provides;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class ShootingStarsToClipboardPlugin extends Plugin
{
	private static final int STAR_BOX_WIDGET_GROUP = 229;
	private static final int STAR_BOX_WIDGET_CHILD = 1;

	private boolean doProcessWidget = false;

	@Inject
	private Client client;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ShootingStarsToClipboardConfig config;

	@Provides
	ShootingStarsToClipboardConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShootingStarsToClipboardConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (doProcessWidget)
		{
			Widget starBox = client.getWidget(STAR_BOX_WIDGET_GROUP, STAR_BOX_WIDGET_CHILD);
			if (starBox != null)
			{
				String text = Text.removeTags(starBox.getText().replace("<br>", " "));

				System.out.println(text);

				Toolkit.getDefaultToolkit()
					.getSystemClipboard()
					.setContents(new StringSelection(text), null);

				sendChatMessage("Shooting Stars info copied to clipboard.");
			}


			doProcessWidget = false;
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == STAR_BOX_WIDGET_GROUP)
		{
			doProcessWidget = true;
		}
	}

	private void sendChatMessage(final String message)
	{
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.CONSOLE)
			.runeLiteFormattedMessage(message)
			.build());
	}

	//You see a shooting star! The star looks like it will land on Crandor<br>or Karamja in the next 39 to 41 minutes.
}
