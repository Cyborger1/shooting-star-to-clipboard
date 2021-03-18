package com.shootingstarstoclipboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimesToShow
{
	ALL("All"),
	FROM_TO_ONLY("From/To Only"),
	ETA_ONLY("ETA Only")
	;

	private final String name;

	@Override
	public String toString()
	{
		return getName();
	}
}
