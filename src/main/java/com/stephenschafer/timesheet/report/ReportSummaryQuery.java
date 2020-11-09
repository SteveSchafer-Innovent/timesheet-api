package com.stephenschafer.timesheet.report;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.stephenschafer.timesheet.EventRow;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportSummaryQuery {
	@Autowired
	private EventQuery eventQuery;
	private long start;
	private long end;

	public static Calendar getCalendar(final Date date) {
		final Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal;
	}

	public static Calendar removeTime(final Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static Date removeTime(final Date date) {
		return removeTime(getCalendar(date)).getTime();
	}

	public static long addDays(final long longDate, final int days) {
		final org.joda.time.DateTime date = new org.joda.time.DateTime(longDate);
		final org.joda.time.DateTime newDate = date.plusDays(days);
		return newDate.getMillis();
	}

	public static int getDateDiff(final long later, final long earlier) {
		// note: dividing by 1000*60*60*24 has problems with daylight savings time
		final org.joda.time.DateTime laterDate = new org.joda.time.DateTime(later);
		final org.joda.time.DateTime earlierDate = new org.joda.time.DateTime(earlier);
		final int days = Days.daysBetween(earlierDate, laterDate).getDays();
		log.info(" days between " + earlierDate + " and " + laterDate + " = " + days);
		return days;
	}

	@Autowired
	private ProjectIdQuery projectIdQuery;

	public ResolvedProjectList getProjectsForEvent(final int eventId) {
		final List<ResolvedProject> resultList = new ArrayList<>();
		projectIdQuery.setArguments(new Object[] { Integer.valueOf(eventId) });
		projectIdQuery.getStream().forEach(projectIdInteger -> {
			final ResolvedProject project = resolveProject(getProject(projectIdInteger.intValue()));
			resultList.add(project);
		});
		return new ResolvedProjectList(resultList);
	}

	public ResolvedProject resolveProject(final RawProject rawProject) {
		if (rawProject == null) {
			return null;
		}
		final ResolvedProject parent = rawProject.isRoot() ? null
			: resolveProject(getProject(rawProject.getParentId()));
		return new ResolvedProject(rawProject.getId(), parent, rawProject.getCode());
	}

	@Autowired
	private RawProjectQuery rawProjectQuery;

	public RawProject getProject(final int projectId) {
		rawProjectQuery.setProjectId(projectId);
		final Optional<RawProject> result = rawProjectQuery.getStream().findFirst();
		return result.orElse(null);
	}

	private static class EventHolder {
		EventRow event = null;
	}

	public Stream<ReportSummaryRow> getStream(final Date startDate, final int userId) {
		eventQuery.setArguments(new Object[] { startDate, Integer.valueOf(userId) });
		this.start = startDate.getTime();
		this.end = addDays(start, 7); // start + 1000L * 60L * 60L * 24L * 7L; // a week
		final AtomicBoolean oneExtra = new AtomicBoolean(false);
		final EventHolder previousEventHolder = new EventHolder();
		final Map<Integer, ResolvedProject> projects = new HashMap<>();
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
				final DecimalFormat df = new DecimalFormat("#0.00");
				log.info("setting duration " + df.format(duration / 1000.0 / 60.0 / 60.0) + " on "
					+ new Date(event.getTime()));
				previousEventHolder.event.setDuration(duration);
				event.setDuration(System.currentTimeMillis() - previousEventHolder.event.getTime());
			}
			previousEventHolder.event = event;
			eventList.add(event);
		});
		eventList.forEach(event -> {
			final ResolvedProjectList eventProjects = getProjectsForEvent(event.getId());
			final long eventDuration = event.getDuration();
			final Date eventDate = removeTime(new Date(event.getTime()));
			long remainingDuration = eventDuration;
			final int divisor = eventProjects.size();
			final int count = divisor;
			for (final ResolvedProject eventProject : eventProjects) {
				long projectEventDuration;
				if (count == 1) {
					projectEventDuration = remainingDuration;
				}
				else {
					projectEventDuration = eventDuration / divisor;
					remainingDuration -= projectEventDuration;
				}
				final Integer key = Integer.valueOf(eventProject.getId());
				ResolvedProject project = projects.get(key);
				if (project == null) {
					project = eventProject;
					projects.put(key, project);
				}
				project.addDuration(eventDate, projectEventDuration);
			}
		});
		final List<ReportSummaryRow> list = new ArrayList<>();
		int maxDepth = 0;
		try {
			for (final Integer key : projects.keySet()) {
				final ResolvedProject project = projects.get(key);
				final List<ResolvedProject> path = project.getPath();
				final List<String> projectCodes = new ArrayList<>();
				for (final ResolvedProject parentProject : path) {
					projectCodes.add(parentProject.getCode());
				}
				final int depth = projectCodes.size();
				if (maxDepth < depth) {
					maxDepth = depth;
				}
				final int dateCount = getDateCount();
				final List<Long> durations = new ArrayList<>();
				for (int i = 0; i < dateCount; i++) {
					durations.add(Long.valueOf(0));
				}
				final Map<Date, Long> dateTotals = project.getDateTotals();
				for (final Date date : dateTotals.keySet()) {
					final Long value = dateTotals.get(date);
					final int i = getDateDiff(date.getTime(), start);
					if (i < durations.size()) {
						durations.set(i, value);
					}
					else {
						log.info("Cannot set duration at index " + i);
					}
				}
				final ReportSummaryRow row = new ReportSummaryRow(projectCodes, durations);
				list.add(row);
			}
		}
		catch (final Exception e) {
			log.error("Failed to generate report rows", e);
		}
		Collections.sort(list);
		return list.stream();
	}

	public int getDateCount() {
		return getDateDiff(end, start);
	}
}
