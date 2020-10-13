package com.stephenschafer.timesheet;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventDaoImpl implements EventDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int deleteById(final int id) {
		return jdbcTemplate.update("delete from event where id = ?", id);
	}

	@Override
	public Optional<Event> findById(final int id) {
		return jdbcTemplate.queryForObject(
			"select time, offset, user, comment from event where id = ?", new Object[] { id },
			(rs, rowNum) -> {
				return Optional.of(
					new Event(id, rs.getTimestamp(1), rs.getInt(2), rs.getInt(3), rs.getString(4)));
			});
	}

	@Override
	public Event add(final Event event) {
		log.info("add event");
		final PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(
				"insert into event (time, comment, offset, user) values (?, ?, ?, ?)",
				Types.TIMESTAMP, Types.VARCHAR, Types.INTEGER, Types.INTEGER);
		factory.setReturnGeneratedKeys(true);
		factory.setGeneratedKeysColumnNames("id");
		final PreparedStatementCreator creator = factory.newPreparedStatementCreator(new Object[] {
			event.getDatetime(), event.getComment(), event.getOffset(), event.getUserId() });
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		log.info("creator = " + creator);
		log.info("keyHolder = " + keyHolder);
		final int rowCount = jdbcTemplate.update(creator, keyHolder);
		log.info("rowCount = " + rowCount);
		if (rowCount == 0) {
			return null;
		}
		final Number generatedId = keyHolder.getKey();
		return new Event(generatedId.intValue(), event.getDatetime(), event.getOffset(),
				event.getUserId(), event.getComment());
	}

	@Override
	public void update(final Event event) {
		log.info("EventDaoImpl.update " + event);
		final PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(
				"update event set time = ?, comment = ?, offset = ?, user = ? where id = ?",
				Types.TIMESTAMP, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER);
		PreparedStatementCreator creator;
		try {
			creator = factory.newPreparedStatementCreator(new Object[] { event.getDatetime(),
				event.getComment(), event.getOffset(), event.getUserId(), event.getId() });
		}
		catch (final Exception e) {
			log.error("failed to get creator");
			e.printStackTrace();
			return;
		}
		log.info("update creator = " + creator);
		jdbcTemplate.update(creator);
		return;
	}

	private static class PreparedStatementHolder {
		PreparedStatement statement;
	}

	@Override
	public void getByDate(final Date startDate, final int userId, final Consumer<Event> consumer) {
		final PreparedStatementHolder holder = new PreparedStatementHolder();
		final String sql = "select id, time, offset, user, comment from event where time >= ? and user = ? order by time";
		final PreparedStatementCreator creator = connection -> {
			holder.statement = connection.prepareStatement(sql);
			holder.statement.setTimestamp(1, new Timestamp(startDate.getTime()));
			holder.statement.setInt(2, userId);
			return holder.statement;
		};
		try {
			jdbcTemplate.query(creator, rs -> {
				consumer.accept(new Event(rs.getInt(1), rs.getTimestamp(2), rs.getInt(3),
						rs.getInt(4), rs.getString(5)));
			});
		}
		catch (final StopException e) {
			try {
				holder.statement.cancel();
			}
			catch (final SQLException e1) {
				log.error("getByDate failed", e1);
			}
		}
	}

	@Override
	public Optional<Integer> countOfEventsByProject(final int id) {
		final String sql = "select count(*) from event_project where project_id = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] { id }, (rs, rowNum) -> {
			return Optional.of(rs.getInt(1));
		});
	}
}
