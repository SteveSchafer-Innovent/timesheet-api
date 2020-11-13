package com.stephenschafer.timesheet;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface NetsuiteClientDao extends CrudRepository<NetsuiteClientEntity, Integer> {
	Optional<NetsuiteClientEntity> findByName(String clientString);
}
