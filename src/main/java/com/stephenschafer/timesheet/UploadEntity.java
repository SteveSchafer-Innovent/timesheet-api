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
@Table(name = "upload")
@NamedNativeQueries({
	@NamedNativeQuery(name = "UploadEntity.report", query = "SELECT u.id, u.date_time, f.name, min(ne.date) as min_date, max(ne.date) as max_date"
		+ " FROM upload u" + " INNER JOIN file f on f.id = u.file_id"
		+ " INNER JOIN netsuite_event ne on ne.upload_id = u.id" + " GROUP BY u.id"
		+ " ORDER BY u.id desc") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UploadEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "file_id")
	private Integer fileId;
	@Column(name = "date_time")
	private Date datetime;
}
