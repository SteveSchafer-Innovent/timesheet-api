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
public class EditedEvent {
	private int id;
	private Date datetime;
	private int offset;
	private String comment;
	private List<Integer> projects;

	public Event getEvent(final int userId) {
		return new Event(id, datetime, offset, userId, comment);
	}

	public EditedEventWithAncestry getEventWithAncestry(final List<List<Integer>> projectIds) {
		return new EditedEventWithAncestry(id, datetime, offset, comment, projectIds);
	}

	public PostedEvent getPostedEvent() {
		return new PostedEvent(datetime, offset, comment, projects);
	}
}
