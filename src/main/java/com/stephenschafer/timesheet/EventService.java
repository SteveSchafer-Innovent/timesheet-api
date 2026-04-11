package com.stephenschafer.timesheet;

import java.util.Date;
import java.util.List;

public interface EventService {
	EditedEvent insert(PostedEvent event, int userId);

	EditedEvent update(EditedEvent event, int userId);

	List<ReportEvent> findByDay(Date day, int userId);

	List<RawEvent> findRawEventsByDay(Date day, int userId);

	int delete(int id);

	EditedEvent findById(int id);

	List<List<Integer>> getProjectAncestry(final List<Integer> projectIds);

	List<List<Integer>> getLastProjects(Date dateTime, int count, int userId);
}
