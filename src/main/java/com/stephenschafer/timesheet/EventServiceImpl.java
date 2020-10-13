package com.stephenschafer.timesheet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service(value = "eventService")
public class EventServiceImpl implements EventService {
	@Autowired
	private EventDao eventDao;
	@Autowired
	private ProjectDao projectDao;
	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<ReportEvent> findByDay(final Date date, final int userId) {
		final Date day = getMidnight(date);
		final Date nextDay = getNextDay(day);
		final AtomicBoolean oneExtra = new AtomicBoolean(false);
		final List<Event> events = new ArrayList<>();
		eventDao.getByDate(day, userId, event -> {
			if (event.getDatetime().before(nextDay)) {
				events.add(event);
			}
			else if (!oneExtra.get()) {
				oneExtra.set(true);
				events.add(event);
			}
			else {
				throw new StopException();
			}
		});
		final List<ReportEvent> reportEvents = new ArrayList<>();
		Event prevEventEntity = null;
		for (final Event event : events) {
			if (prevEventEntity != null && prevEventEntity.getDatetime().before(nextDay)) {
				reportEvents.add(getReportEvent(event, prevEventEntity));
			}
			prevEventEntity = event;
		}
		if (prevEventEntity != null && prevEventEntity.getDatetime().before(nextDay)) {
			reportEvents.add(getReportEvent(null, prevEventEntity));
		}
		return reportEvents;
	}

	private ReportEvent getReportEvent(final Event nextEvent, final Event prevEvent) {
		final long nextTime = nextEvent == null ? System.currentTimeMillis()
			: nextEvent.getDatetime().getTime();
		final long duration = nextTime - prevEvent.getDatetime().getTime();
		final List<Integer> projectIds = getProjectIds(prevEvent.getId());
		final List<List<String>> projects = getProjectNames(projectIds);
		return new ReportEvent(prevEvent.getId(), prevEvent.getDatetime(), duration,
				prevEvent.getComment(), projects);
	}

	private Date getNextDay(final Date day) {
		final Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	private Date getMidnight(final Date date) {
		final Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}

	private List<Integer> getProjectIds(final int eventId) {
		log.info("getProjectIds eventId = " + eventId);
		final Query namedQuery = entityManager.createNamedQuery("EventProjectEntity.findByEventId");
		namedQuery.setParameter("eventId", Integer.valueOf(eventId));
		@SuppressWarnings("unchecked")
		final List<Integer> list = namedQuery.getResultList();
		// named query returns a list of Integer in this case
		return list;
	}

	@Override
	public List<List<Integer>> getProjectAncestry(final List<Integer> altProjectIds) {
		log.info("getProjectAncestry projectIds = " + altProjectIds);
		final List<List<Integer>> result = new ArrayList<>();
		for (final Integer projectId : altProjectIds) {
			final List<Integer> levelProjectIds = getProjectAncestry(projectId);
			result.add(levelProjectIds);
		}
		return result;
	}

	private List<List<String>> getProjectNames(final List<Integer> projectIds) {
		log.info("getProjectNames projectIds = " + projectIds);
		final List<List<String>> result = new ArrayList<>();
		for (final Integer projectId : projectIds) {
			final List<String> projectNames = getProjectNames(projectId);
			result.add(projectNames);
		}
		return result;
	}

	private List<Integer> getProjectAncestry(final Integer projectId) {
		final List<Integer> result = new ArrayList<>();
		addProjectAncestry(projectId, result);
		return result;
	}

	private List<String> getProjectNames(final Integer projectId) {
		final List<String> result = new ArrayList<>();
		addProjectName(projectId, result);
		return result;
	}

	private void addProjectName(final Integer projectId, final List<String> list) {
		if (projectId != null) {
			final Optional<ProjectEntity> optionalProjectEntity = projectDao.findById(projectId);
			if (optionalProjectEntity.isPresent()) {
				final ProjectEntity projectEntity = optionalProjectEntity.get();
				addProjectName(projectEntity.getParentId(), list);
				list.add(projectEntity.getCode());
			}
			else {
				list.add("Unknown id " + projectId);
			}
		}
	}

	private void addProjectAncestry(final Integer projectId, final List<Integer> list) {
		if (projectId != null) {
			final Optional<ProjectEntity> optionalProjectEntity = projectDao.findById(projectId);
			if (optionalProjectEntity.isPresent()) {
				final ProjectEntity projectEntity = optionalProjectEntity.get();
				addProjectAncestry(projectEntity.getParentId(), list);
				list.add(projectEntity.getId());
			}
			else {
				list.add(Integer.valueOf(0));
			}
		}
	}

	@Override
	public int delete(final int id) {
		return eventDao.deleteById(id);
	}

	@Override
	public EditedEvent findById(final int eventId) {
		final Optional<Event> optionalEvent = eventDao.findById(eventId);
		if (!optionalEvent.isPresent()) {
			return null;
		}
		final Event event = optionalEvent.get();
		return event.getEditEvent(getProjectIds(eventId));
	}

	@Override
	public EditedEvent update(final EditedEvent editedEvent, final int userId) {
		log.info("EventServiceImpl update " + editedEvent);
		final EditedEvent newEvent = findById(editedEvent.getId());
		if (newEvent == null) {
			log.error("event not found: " + editedEvent.getId());
			throw new RuntimeException("Event " + editedEvent.getId() + " not found");
		}
		eventDao.update(editedEvent.getEvent(userId));
		deleteEventProjectsByEventId(editedEvent.getId());
		insertEventProjects(editedEvent.getId(), editedEvent.getProjects());
		return editedEvent;
	}

	@Override
	public EditedEvent insert(final PostedEvent postedEvent, final int userId) {
		log.info("EventServiceImpl insert " + postedEvent);
		final Event resultEvent = eventDao.add(postedEvent.getEvent(userId));
		log.info("resultEvent id = " + resultEvent.getId());
		deleteEventProjectsByEventId(resultEvent.getId());
		insertEventProjects(resultEvent.getId(), postedEvent.getProjects());
		return resultEvent.getEditEvent(postedEvent.getProjects());
	}

	private void deleteEventProjectsByEventId(final int eventId) {
		log.info("EventServiceImpl deleteEventProjectByEventId " + eventId);
		final Query deleteQuery = entityManager.createNamedQuery(
			"EventProjectEntity.deleteByEventId");
		deleteQuery.setParameter("eventId", Integer.valueOf(eventId));
		final int deleteCount = deleteQuery.executeUpdate();
		log.info("eventProjects deleted: " + deleteCount);
	}

	private void insertEventProjects(final int eventId, final List<Integer> projectIds) {
		log.info("EventServiceImpl insertEventProjects " + eventId);
		for (final Integer projectId : projectIds) {
			log.info("adding eventProject " + projectId);
			final Query insertQuery = entityManager.createNamedQuery("EventProjectEntity.insert");
			insertQuery.setParameter("eventId", Integer.valueOf(eventId));
			insertQuery.setParameter("projectId", projectId);
			final int insertCount = insertQuery.executeUpdate();
			log.info("eventProjects projects inserted = " + insertCount);
		}
	}
}
