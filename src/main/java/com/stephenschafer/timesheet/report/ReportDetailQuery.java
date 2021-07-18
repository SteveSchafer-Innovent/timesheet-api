package com.stephenschafer.timesheet.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.timesheet.EventRow;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportDetailQuery extends ReportQuery {
	@Autowired
	private EventQuery eventQuery;

	public Stream<ReportDetailRow> getStream(final Date startDate, final Date endDate,
			final int userId) {
		log.info("getStream " + startDate + ", " + endDate);
		eventQuery.setArguments(
			new Object[] { startDate == null ? new Date(0L) : startDate, Integer.valueOf(userId) });
		final long end = endDate == null ? Long.MAX_VALUE : endDate.getTime();
		final AtomicBoolean oneExtra = new AtomicBoolean(false);
		final EventHolder previousEventHolder = new EventHolder();
		final List<EventRow> eventList = new ArrayList<>();
		// set the duration on each event
		eventQuery.getStream().forEach(event -> {
			if (event.getTime() >= end) {
				if (oneExtra.get()) {
					return;
				}
				oneExtra.set(true);
			}
			if (previousEventHolder.event != null) {
				final long duration = event.getTime() - previousEventHolder.event.getTime();
				previousEventHolder.event.setDuration(duration);
				event.setDuration(System.currentTimeMillis() - previousEventHolder.event.getTime());
			}
			previousEventHolder.event = event;
			eventList.add(event);
		});
		final List<ReportDetailRow> list = new ArrayList<>();
		eventList.forEach(event -> {
			final ResolvedProjectList eventProjects = getProjectsForEvent(event.getId());
			final long eventDuration = event.getDuration();
			long remainingDuration = eventDuration;
			final int divisor = eventProjects.size();
			for (final ResolvedProject eventProject : eventProjects) {
				long projectEventDuration;
				if (divisor == 1) {
					projectEventDuration = remainingDuration;
				}
				else {
					projectEventDuration = eventDuration / divisor;
					remainingDuration -= projectEventDuration;
				}
				final List<ResolvedProject> path = eventProject.getPath();
				final List<ReportProject> reportProjects = new ArrayList<>();
				for (final ResolvedProject parentProject : path) {
					reportProjects.add(
						new ReportProject(parentProject.getId(), parentProject.getCode()));
				}
				final ReportDetailRow row = new ReportDetailRow(reportProjects,
						new Date(event.getTime()), projectEventDuration);
				list.add(row);
			}
		});
		Collections.sort(list);
		return list.stream();
	}
}
