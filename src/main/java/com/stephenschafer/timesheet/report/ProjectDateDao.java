package com.stephenschafer.timesheet.report;

import org.springframework.data.repository.CrudRepository;

public interface ProjectDateDao extends CrudRepository<ProjectDateEntity, ProjectDateKey> {
}
