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
public class Event {
	private int id;
	private Date datetime;
	private int offset;
	private int userId;
	private String comment;

	public EditedEvent getEditEvent(final List<Integer> projects) {
		return new EditedEvent(id, datetime, offset, comment, projects);
	}
}
