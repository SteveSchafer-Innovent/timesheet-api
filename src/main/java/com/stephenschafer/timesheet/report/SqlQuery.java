package com.stephenschafer.timesheet.report;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SqlQuery<T> {
	public Map<String, String> substitutions = null;
	public Object[] arguments = null;
	public final String query;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public SqlQuery(final String query) {
		this.query = query;
	}

	public void setSubstitutions(final Map<String, String> substitutions) {
		this.substitutions = substitutions;
	}

	public void setArguments(final Object[] arguments) {
		this.arguments = arguments;
	}

	public Stream<T> getStream() {
		final PreparedStatementCreator creator = connection -> {
			String query = this.query;
			if (substitutions != null) {
				for (final String key : substitutions.keySet()) {
					final String value = substitutions.get(key);
					query = query.replace(key, value);
				}
			}
			final PreparedStatement statement = connection.prepareStatement(query);
			if (arguments != null) {
				for (int i = 0; i < arguments.length; i++) {
					final Object argument = arguments[i];
					statement.setObject(i + 1, argument);
				}
			}
			return statement;
		};
		final Stream.Builder<T> streamBuilder = Stream.builder();
		jdbcTemplate.query(creator, resultSet -> {
			try {
				streamBuilder.accept(createDataRow(resultSet));
			}
			catch (final SQLException e) {
				log.error("createDataRow failed", e);
			}
		});
		return streamBuilder.build();
	}

	abstract protected T createDataRow(ResultSet resultSet) throws SQLException;
}
