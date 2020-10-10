package com.stephenschafer.timesheet.report;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.stephenschafer.timesheet.EventRow;

public class EventQuery extends SqlQuery<EventRow> {
	public EventQuery() {
		super("select id, time, comment from event where time >= ? and user = ? order by 2");
	}

	@Override
	protected EventRow createDataRow(final ResultSet resultSet) throws SQLException {
		final EventRow event = new EventRow(resultSet.getInt(1),
				resultSet.getTimestamp(2).getTime(), resultSet.getString(3));
		return event;
	}
}
