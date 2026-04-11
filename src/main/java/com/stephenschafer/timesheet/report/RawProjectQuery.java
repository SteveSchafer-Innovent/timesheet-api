package com.stephenschafer.timesheet.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

import com.stephenschafer.timesheet.RawProject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawProjectQuery extends SqlQuery<RawProject> {
	private int projectId;

	public RawProjectQuery() {
		super("select code, minimum_billable_hours, round_daily_hours_to, bigtime_project_id, bigtime_task_id, bigtime_description, parent_id from project where id = ?");
	}

	public void setProjectId(final int projectId) {
		this.projectId = projectId;
	}

	@Override
	public Stream<RawProject> getStream() {
		this.setArguments(new Object[] { Integer.valueOf(projectId) });
		return super.getStream();
	}

	@Override
	protected RawProject createDataRow(final ResultSet resultSet) throws SQLException {
		int i = 0;
		final String code = resultSet.getString(++i);
		final double minimumBillableHours = resultSet.getDouble(++i);
		final double roundDailyHoursTo = resultSet.getDouble(++i);
		final int bigtimeProjectId = resultSet.getInt(++i);
		final int bigtimeTaskId = resultSet.getInt(++i);
		final String bigtimeDescription = resultSet.getString(++i);
		final int parentId = resultSet.getInt(++i);
		final boolean isRoot = resultSet.wasNull();
		final double minDurationDbl = minimumBillableHours * 60.0 * 60.0 * 1000.0;
		final double roundDbl = roundDailyHoursTo * 60.0 * 60.0 * 1000.0;
		return new RawProject(projectId, isRoot, parentId, code,
				Double.valueOf(minDurationDbl).longValue(), Double.valueOf(roundDbl).longValue(),
				bigtimeProjectId, bigtimeTaskId, bigtimeDescription);
	}
}
