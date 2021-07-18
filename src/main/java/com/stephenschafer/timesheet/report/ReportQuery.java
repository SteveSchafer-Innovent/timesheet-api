package com.stephenschafer.timesheet.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.stephenschafer.timesheet.EventRow;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportQuery {
	@Autowired
	private ProjectIdQuery projectIdQuery;
	@Autowired
	private RawProjectQuery rawProjectQuery;

	protected RawProject getProject(final int projectId) {
		rawProjectQuery.setProjectId(projectId);
		final Optional<RawProject> result = rawProjectQuery.getStream().findFirst();
		return result.orElse(null);
	}

	protected ResolvedProject resolveProject(final RawProject rawProject) {
		if (rawProject == null) {
			return null;
		}
		final ResolvedProject parent = rawProject.isRoot() ? null
			: resolveProject(getProject(rawProject.getParentId()));
		return new ResolvedProject(rawProject.getId(), parent, rawProject.getCode());
	}

	protected ResolvedProjectList getProjectsForEvent(final int eventId) {
		final List<ResolvedProject> resultList = new ArrayList<>();
		projectIdQuery.setArguments(new Object[] { Integer.valueOf(eventId) });
		projectIdQuery.getStream().forEach(projectIdInteger -> {
			final ResolvedProject project = resolveProject(getProject(projectIdInteger.intValue()));
			resultList.add(project);
		});
		return new ResolvedProjectList(resultList);
	}

	protected static class EventHolder {
		EventRow event = null;
	}

	protected static Calendar getCalendar(final Date date) {
		final Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal;
	}

	protected static Calendar removeTime(final Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	protected static Date removeTime(final Date date) {
		return removeTime(getCalendar(date)).getTime();
	}

	protected static long addDays(final long longDate, final int days) {
		final org.joda.time.DateTime date = new org.joda.time.DateTime(longDate);
		final org.joda.time.DateTime newDate = date.plusDays(days);
		return newDate.getMillis();
	}

	protected static int getDateDiff(final long later, final long earlier) {
		// note: dividing by 1000*60*60*24 has problems with daylight savings time
		final org.joda.time.DateTime laterDate = new org.joda.time.DateTime(later);
		final org.joda.time.DateTime earlierDate = new org.joda.time.DateTime(earlier);
		final int days = Days.daysBetween(earlierDate, laterDate).getDays();
		log.info(" days between " + earlierDate + " and " + laterDate + " = " + days);
		return days;
	}
}
