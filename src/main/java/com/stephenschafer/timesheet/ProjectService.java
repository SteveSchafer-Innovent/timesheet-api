package com.stephenschafer.timesheet;

import java.util.List;

public interface ProjectService {
	ProjectEntity insert(ProjectEntity project);

	ProjectEntity update(ProjectEntity project);

	List<FindProjectResult> findByParent(Integer parentId);

	List<FindProjectResult> findByRoot();

	void delete(int id);

	ProjectEntity findById(int id);
}
