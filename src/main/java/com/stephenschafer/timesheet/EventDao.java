package com.stephenschafer.timesheet;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

public interface EventDao {
	int deleteById(int id);

	Optional<Event> findById(int id);

	Event add(Event event);

	void update(Event event);

	void getByDate(Date startDate, int userId, Consumer<Event> consumer);
}
