package com.stephenschafer.timesheet.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ReportProject {
	private int id;
	private String code;
	private long minDuration;
	private long round;
	private int bigtimeProjectId;
	private int bigtimeTaskId;
	private String bigtimeDescription;
}
