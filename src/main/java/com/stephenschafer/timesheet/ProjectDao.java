package com.stephenschafer.timesheet;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDao extends CrudRepository<ProjectEntity, Integer> {
}