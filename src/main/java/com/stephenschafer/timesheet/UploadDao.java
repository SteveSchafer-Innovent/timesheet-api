package com.stephenschafer.timesheet;

import org.springframework.data.repository.CrudRepository;

public interface UploadDao extends CrudRepository<UploadEntity, Integer> {
}
