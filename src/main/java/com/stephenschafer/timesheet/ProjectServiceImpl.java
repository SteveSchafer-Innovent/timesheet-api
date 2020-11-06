package com.stephenschafer.timesheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service(value = "projectService")
public class ProjectServiceImpl implements ProjectService {
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private EventDao eventDao;
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ProjectEntity insert(final ProjectEntity project) {
		return projectDao.save(project);
	}

	@Override
	public ProjectEntity update(final ProjectEntity project) {
		return projectDao.save(project);
	}

	@Override
	public List<FindProjectResult> findByParent(final Integer parentId) {
		final Query namedQuery = entityManager.createNamedQuery("ProjectEntity.findByParentId");
		namedQuery.setParameter("parentId", parentId);
		return convertToProjects(namedQuery.getResultList());
	}

	@Override
	public List<FindProjectResult> findByRoot() {
		final Query namedQuery = entityManager.createNamedQuery("ProjectEntity.findByRoot");
		return convertToProjects(namedQuery.getResultList());
	}

	private List<FindProjectResult> convertToProjects(final List<?> resultList) {
		final List<FindProjectResult> results = new ArrayList<>();
		for (final Object project : resultList) {
			if (project instanceof Object[]) {
				final Object[] objects = (Object[]) project;
				if (objects.length >= 2) {
					final Object object0 = objects[0];
					if (object0 instanceof Integer) {
						final Integer id = (Integer) object0;
						final Object object1 = objects[1];
						if (object1 instanceof String) {
							final String code = (String) object1;
							final FindProjectResult result = new FindProjectResult();
							result.setId(id);
							result.setCode(code);
							results.add(result);
						}
						else {
							log.error(
								"Second element of the object array returned from ProjectEntity.findByParentId is not a String");
						}
					}
					else {
						log.error(
							"First element of the object array returned from ProjectEntity.findByParentId is not an Integer");
					}
				}
				else {
					log.error(
						"Object array returned from ProjectEntity.findByParentId has zero length");
				}
			}
			else {
				log.error(
					"Object returned from ProjectEntity.findByParentId was not an Object[].  It was "
						+ project.getClass().getName());
			}
		}
		return results;
	}

	@Override
	public int delete(final int id) {
		log.info("ProjectService.delete " + id);
		int count = 0;
		try {
			count += deleteChildren(id);
			projectDao.deleteById(id);
			count++;
		}
		catch (final Exception e) {
			log.error("Failed to delete project " + id, e);
			throw e;
		}
		return count;
	}

	@Override
	public boolean canDelete(final int id) {
		final Optional<Integer> countOfEvents = eventDao.countOfEventsByProject(id);
		if (!countOfEvents.isPresent()) {
			log.info("countOfEventsByProject result not present");
			return false;
		}
		final Integer count = countOfEvents.get();
		if (count.intValue() > 0) {
			return false;
		}
		// look at child projects
		final List<FindProjectResult> list = findByParent(Integer.valueOf(id));
		for (final FindProjectResult project : list) {
			if (!canDelete(project.getId())) {
				return false;
			}
		}
		return true;
	}

	private int deleteChildren(final int parentId) {
		final List<FindProjectResult> list = findByParent(Integer.valueOf(parentId));
		int count = 0;
		for (final FindProjectResult project : list) {
			count += delete(project.getId());
		}
		return count;
	}

	@Override
	public ProjectEntity findById(final int id) {
		final Optional<ProjectEntity> optionalProjectEntity = projectDao.findById(id);
		return optionalProjectEntity.isPresent() ? optionalProjectEntity.get() : null;
	}

	@Override
	public List<Integer> getAncestry(final int id) {
		log.info("getAncestry id = " + id);
		final List<Integer> result = new ArrayList<>();
		final Optional<ProjectEntity> optionalProjectEntity = projectDao.findById(id);
		if (optionalProjectEntity.isPresent()) {
			final ProjectEntity projectEntity = optionalProjectEntity.get();
			if (projectEntity.parentId != null) {
				addToAncestry(projectEntity.parentId, result);
			}
		}
		return result;
	}

	private void addToAncestry(final Integer id, final List<Integer> list) {
		final Optional<ProjectEntity> optionalProjectEntity = projectDao.findById(id);
		if (optionalProjectEntity.isPresent()) {
			final ProjectEntity projectEntity = optionalProjectEntity.get();
			if (projectEntity.parentId != null) {
				addToAncestry(projectEntity.parentId, list);
			}
			list.add(id);
		}
	}
}
