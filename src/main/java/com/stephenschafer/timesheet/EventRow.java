package com.stephenschafer.timesheet;

public class EventRow {
	private final int id;
	private final long time;
	private final String comment;
	private long duration;

	public EventRow(final int id, final long time, final String comment) {
		this.id = id;
		this.time = time;
		this.comment = comment;
	}

	public int getId() {
		return id;
	}

	public long getTime() {
		return time;
	}

	public String getComment() {
		return comment;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
	}
}
