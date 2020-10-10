package com.stephenschafer.timesheet;

import org.springframework.data.repository.CrudRepository;

public interface EventProjectDao extends CrudRepository<EventProject, EventProjectKey> {
}