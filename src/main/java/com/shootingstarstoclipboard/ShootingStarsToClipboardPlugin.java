package com.shootingstarstoclipboard;

import com.google.inject.Provides;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
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
	name = "Stars to Clipboard",
	description = "Puts shooting stars telescope predictions in your clipboard",
	tags = {"Shooting", "Stars", "Star", "Clipboard"}
)
public class ShootingStarsToClipboardPlugin extends Plugin
{
	private static final int STAR_BOX_WIDGET_GROUP = 229;
	private static final int STAR_BOX_WIDGET_CHILD = 1;

	// Close your eyes and pray for this regex to work
	// Group 1 = Truncated
	// Group 2 = Loc 1
	// Group 3 = Loc 2
	// Group 4 = Hours 1
	// Group 5 = Minutes 1
	// Group 6 = Hours 2
	// Group 7 = Minutes 2
	private static final Pattern STAR_PATTERN = Pattern.compile(
		"^You see a shooting star! " +
			"The star looks like it will land " +
			"((?:in |on )(?:the )?((?:(?!\\bor\\b)[\\w ])+)" +
			"(?: or (?:in |on )?(?:the )?([\\w ]+))? " +
			"in the next (?:(\\d+) hours? )?(?:(\\d+)(?: minutes?)? )?" +
			"to (?:(\\d+) hours? )?(?:(\\d+) minutes)?\\.)$");

	private boolean doProcessWidget;

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
		doProcessWidget = false;
	}

	@Override
	protected void shutDown() throws Exception
	{
		doProcessWidget = false;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (doProcessWidget)
		{
			doProcessWidget = false;

			Widget starBox = client.getWidget(STAR_BOX_WIDGET_GROUP, STAR_BOX_WIDGET_CHILD);
			if (starBox != null)
			{
				Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
				String text = Text.removeTags(starBox.getText().replace("<br>", " "));

				Matcher m = STAR_PATTERN.matcher(text);

				if (!m.matches())
				{
					return;
				}

				Instant from = getTime(now, m.group(4), m.group(5));
				Instant to = getTime(now, m.group(6), m.group(7));
				Instant eta = from.plus(Duration.between(from, to).dividedBy(2));

				String datePattern = "HH:mm";
				if (!config.useUTCTimes())
				{
					datePattern += " Z";
				}
				if (!config.useShortTimes())
				{
					datePattern = "yyyy-MM-dd " + datePattern;
				}
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern)
					.withZone(config.useUTCTimes() ? ZoneId.from(ZoneOffset.UTC) : ZoneId.systemDefault())
					.withLocale(Locale.UK);

				int world = client.getWorld();

				String copyText;
				switch (config.copyFormat())
				{
					case TRUNCATED_TEXT:
						String t = m.group(1);
						copyText = t.substring(0, 1).toUpperCase() + t.substring(1) + " " + getSuffixText(world, formatter, from, to, eta);
						break;
					case TAB_DELIMITED:
						copyText = getDelimitedText(world, m.group(2), m.group(3), "\t", formatter, from, to, eta);
						break;
					case COMMA_DELIMITED:
						copyText = getDelimitedText(world, m.group(2), m.group(3), ", ", formatter, from, to, eta);
						break;
					default:
						copyText = text + " " + getSuffixText(world, formatter, from, to, eta);
						break;
				}

				Toolkit.getDefaultToolkit()
					.getSystemClipboard()
					.setContents(new StringSelection(copyText), null);

				sendChatMessage(String.format("Shooting Stars info copied to clipboard for world %d.", world));
			}
		}
	}

	private String getDelimitedText(int world, String loc1, String loc2, String delim, DateTimeFormatter formatter, Instant from, Instant to, Instant eta)
	{
		String loc = loc1;
		if (loc2 != null)
		{
			loc = loc + "/" + loc2;
		}

		TimesToShow t = config.timesToShow();

		StringBuilder sb = new StringBuilder();
		sb.append(world);
		sb.append(delim).append(loc);

		if (t == TimesToShow.ALL || t == TimesToShow.FROM_TO_ONLY)
		{
			sb.append(delim).append(formatter.format(from))
				.append(delim).append(formatter.format(to));
		}

		if (t == TimesToShow.ALL || t == TimesToShow.ETA_ONLY)
		{
			sb.append(delim).append(formatter.format(eta));
		}

		return sb.toString();
	}

	private String getSuffixText(int world, DateTimeFormatter formatter, Instant from, Instant to, Instant eta)
	{
		TimesToShow t = config.timesToShow();
		StringBuilder sb = new StringBuilder();

		sb.append("World: ").append(world);

		if (t == TimesToShow.ALL || t == TimesToShow.FROM_TO_ONLY)
		{
			sb.append(", From: ").append(formatter.format(from))
				.append(", To: ").append(formatter.format(to));
		}

		if (t == TimesToShow.ALL || t == TimesToShow.ETA_ONLY)
		{
			sb.append(", ETA: ").append(formatter.format(eta));
		}

		return sb.toString();
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

	private Instant getTime(Instant baseTime, String h, String m)
	{
		final int hours = h != null ? Integer.parseInt(h) : 0;
		final int minutes = m != null ? Integer.parseInt(m) : 0;

		return baseTime.plus(Duration.ofHours(hours)).plus(Duration.ofMinutes(minutes));
	}

	/*
	You see a shooting star! The star looks like it will land on Crandor or Karamja in the next 39 to 41 minutes.
	You see a shooting star! The star looks like it will land in the Kharidian Desert in the next 1 hour 34 minutes to 1 hour 36 minutes.
	You see a shooting star! The star looks like it will land in the Feldip Hills or on the Isle of Souls in the next 1 hour 51 minutes to 1 hour 53 minutes.
	You see a shooting star! The star looks like it will land in Morytania in the next 1 hour 26 minutes to 1 hour 28 minutes.
	You see a shooting star! The star looks like it will land in the Wilderness in the next 1 hour 31 minutes to 1 hour 33 minutes.
	You see a shooting star! The star looks like it will land in Tirannwn in the next 1 hour 24 minutes to 1 hour 26 minutes.
	You see a shooting star! The star looks like it will land in Great Kourend in the next 1 hour 29 minutes to 1 hour 31 minutes.
	You see a shooting star! The star looks like it will land in Kandarin in the next 1 hour 23 minutes to 1 hour 25 minutes.
	You see a shooting star! The star looks like it will land in Kandarin in the next 52 to 54 minutes.
	You see a shooting star! The star looks like it will land in Misthalin in the next 1 hour 3 minutes to 1 hour 5 minutes.
	You see a shooting star! The star looks like it will land in Misthalin in the next 1 hour 0 minutes to 1 hour 2 minutes.
	You see a shooting star! The star looks like it will land in Misthalin in the next 59 minutes to 1 hour 1 minutes.
	 */

	//^You see a shooting star! The star looks like it will land (?:in |on )(?:the )?([\w ]+) in the next (?:(\d+) hours? )?(?:(\d+)(?: minutes?)? )?to (?:(\d+) hours? )?(?:(\d+) minutes)?\.$
	//^You see a shooting star! The star looks like it will land ((?:in |on )(?:the )?((?:(?!\bor\b)[\w ])+)(?: or (?:in |on )?(?:the )?([\w ]+))? in the next (?:(\d+) hours? )?(?:(\d+)(?: minutes?)? )?to (?:(\d+) hours? )?(?:(\d+) minutes)?\.)$
}
