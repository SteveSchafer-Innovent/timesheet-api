package com.stephenschafer.timesheet;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class UploadReportRow {
	private final Integer uploadId;
	private final Date uploadDate;
	private final String filename;
	private final Date minDate;
	private final Date maxDate;
}
