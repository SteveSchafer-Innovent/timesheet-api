package com.stephenschafer.timesheet;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface NetsuiteTaskDao extends CrudRepository<NetsuiteTaskEntity, Integer> {
	Optional<NetsuiteTaskEntity> findByProjectIdAndName(Integer projectId, String name);
}
