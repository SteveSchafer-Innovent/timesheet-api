package com.stephenschafer.timesheet;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "netsuite_event")
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
