package com.stephenschafer.timesheet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "project")
@NamedNativeQueries({
	@NamedNativeQuery(name = "ProjectEntity.findByParentId", query = "SELECT p.id, p.code FROM project p WHERE p.parent_id=:parentId"),
	@NamedNativeQuery(name = "ProjectEntity.findByRoot", query = "SELECT p.id, p.code FROM project p WHERE p.parent_id is null") })
@Getter
@Setter
@ToString
public class ProjectEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	@Column(name = "parent_id")
	Integer parentId;
	String code;
}