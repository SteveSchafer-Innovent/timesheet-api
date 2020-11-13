package com.stephenschafer.timesheet;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface NetsuiteProjectDao extends CrudRepository<NetsuiteProjectEntity, Integer> {
	Optional<NetsuiteProjectEntity> findByClientIdAndName(Integer clientId, String name);
}
