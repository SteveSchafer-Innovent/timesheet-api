package com.stephenschafer.timesheet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class NetsuiteTaskReportRow {
	private final String client;
	private final String project;
	private final String task;
	private final Double hours;
}
