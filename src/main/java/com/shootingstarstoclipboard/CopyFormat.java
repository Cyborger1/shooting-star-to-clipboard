package com.shootingstarstoclipboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CopyFormat
{
	FULL_TEXT("Full Text"),
	COMMA_DELIMITED("Comma Delimited"),
	TAB_DELIMITED("Tab Delimited")
	;

	private final String name;

	@Override
	public String toString()
	{
		return getName();
	}
}
