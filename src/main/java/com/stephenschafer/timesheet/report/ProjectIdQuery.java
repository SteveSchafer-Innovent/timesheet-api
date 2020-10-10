package com.stephenschafer.timesheet.report;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectIdQuery extends SqlQuery<Integer> {
	public ProjectIdQuery() {
		super("select project_id from event_project where event_id = ?");
	}

	@Override
	protected Integer createDataRow(final ResultSet resultSet) throws SQLException {
		return Integer.valueOf(resultSet.getInt(1));
	}
}
