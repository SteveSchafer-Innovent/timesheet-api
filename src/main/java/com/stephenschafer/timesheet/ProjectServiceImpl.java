package com.stephenschafer.timesheet;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service(value = "projectService")
public class ProjectServiceImpl implements ProjectService {
	@Autowired
	private ProjectDao projectDao;
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
		@SuppressWarnings("unchecked")
		final List<FindProjectResult> list = namedQuery.getResultList();
		return list;
	}

	@Override
	public List<FindProjectResult> findByRoot() {
		final Query namedQuery = entityManager.createNamedQuery("ProjectEntity.findByRoot");
		@SuppressWarnings("unchecked")
		final List<FindProjectResult> list = namedQuery.getResultList();
		return list;
	}

	@Override
	public void delete(final int id) {
		projectDao.deleteById(id);
	}

	@Override
	public ProjectEntity findById(final int id) {
		final Optional<ProjectEntity> optionalProjectEntity = projectDao.findById(id);
		return optionalProjectEntity.isPresent() ? optionalProjectEntity.get() : null;
	}
}
