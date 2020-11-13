package com.stephenschafer.timesheet;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface FileDao extends CrudRepository<FileEntity, Integer> {
	Optional<FileEntity> findByName(String filename);
}
