package com.stephenschafer.timesheet;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UploadRow {
	private Integer eventId;
	private Date date;
	private NetsuiteClientEntity client;
	private NetsuiteProjectEntity project;
	private NetsuiteTaskEntity task;
	private String notes;
	private Double hours;
}
