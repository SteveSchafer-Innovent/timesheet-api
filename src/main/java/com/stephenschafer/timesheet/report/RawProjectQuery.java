package com.stephenschafer.timesheet.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawProjectQuery extends SqlQuery<RawProject> {
	private int projectId;

	public RawProjectQuery() {
		super("select code, parent_id from project where id = ?");
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
		final int parentId = resultSet.getInt(++i);
		final boolean isRoot = resultSet.wasNull();
		return new RawProject(projectId, isRoot, parentId, code);
	}
}
