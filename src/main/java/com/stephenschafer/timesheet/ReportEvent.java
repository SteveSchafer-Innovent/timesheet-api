package com.stephenschafer.timesheet;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportEvent {
	final int id;
	final Date time;
	final long duration;
	final String comments;
	final List<List<String>> projects;
}
