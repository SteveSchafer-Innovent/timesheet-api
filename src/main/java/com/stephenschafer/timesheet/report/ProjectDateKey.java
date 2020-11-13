package com.stephenschafer.timesheet.report;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProjectDateKey implements Serializable {
	private static final long serialVersionUID = 1L;
	private int userId;
	private int projectId;
	private String date;
}
