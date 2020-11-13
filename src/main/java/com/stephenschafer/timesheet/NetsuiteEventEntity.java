package com.stephenschafer.timesheet;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "netsuite_event")
@NamedNativeQueries({
	@NamedNativeQuery(name = "NetsuiteEventEntity.taskReport", query = "SELECT c.name as client_name, p.name as project_name, t.name as task_name, sum(e.hours) as hours"
		+ " FROM netsuite_event e" + " INNER JOIN netsuite_task t on t.id = e.task_id"
		+ " INNER JOIN netsuite_project p on p.id = t.project_id"
		+ " INNER JOIN netsuite_client c on c.id = p.client_id" + " GROUP BY e.task_id"
		+ " ORDER BY c.name, p.name, t.name") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NetsuiteEventEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column
	private Date date;
	@Column(name = "task_id")
	private Integer taskId;
	@Column
	private String notes;
	@Column
	private Double hours;
	@Column(name = "upload_id")
	private Integer uploadId;
}
