package com.stephenschafer.timesheet;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class EditedEventWithAncestry {
	private int id;
	private Date datetime;
	private int offset;
	private String comment;
	private List<List<Integer>> projects;

	public Event getEvent(final int userId) {
		return new Event(id, datetime, offset, userId, comment);
	}
}
