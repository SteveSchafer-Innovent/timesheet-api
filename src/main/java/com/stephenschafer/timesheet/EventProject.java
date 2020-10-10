package com.stephenschafer.timesheet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@IdClass(EventProjectKey.class)
@NamedNativeQueries({
	@NamedNativeQuery(name = "EventProjectEntity.findByEventId", query = "SELECT ep.project_id FROM event_project ep WHERE ep.event_id=:eventId"),
	@NamedNativeQuery(name = "EventProjectEntity.deleteByEventId", query = "DELETE FROM event_project WHERE event_id=:eventId"),
	@NamedNativeQuery(name = "EventProjectEntity.insert", query = "INSERT INTO event_project (event_id, project_id) VALUES (:eventId, :projectId)") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventProject {
	@Id
	@Column(name = "event_id")
	private Integer eventId;
	@Id
	@Column(name = "project_id")
	private Integer projectId;
}