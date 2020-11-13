package com.stephenschafer.timesheet.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "project_date")
@IdClass(ProjectDateKey.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProjectDateEntity {
	@Id
	@Column(name = "user_id")
	private int userId;
	@Id
	@Column(name = "project_id")
	private int projectId;
	@Id
	@Column
	private String date;
	@Column
	private long time;
	@Column
	private boolean checked;
}
