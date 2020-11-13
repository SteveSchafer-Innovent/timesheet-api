package com.stephenschafer.timesheet;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface NetsuiteEventDao extends CrudRepository<NetsuiteEventEntity, Integer> {
	List<NetsuiteEventEntity> findByUploadId(Integer uploadId);
}
