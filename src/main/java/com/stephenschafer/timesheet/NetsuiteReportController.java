package com.stephenschafer.timesheet;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class NetsuiteReportController {
	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping("/netsuite-task-report")
	public ApiResponse<List<NetsuiteTaskReportRow>> getNetsuiteTaskReport() {
		log.info("GET /netsuite-task-report");
		final Query namedQuery = entityManager.createNamedQuery("NetsuiteEventEntity.taskReport");
		@SuppressWarnings("unchecked")
		final List<Object[]> list = namedQuery.getResultList();
		final List<NetsuiteTaskReportRow> resultList = new ArrayList<>();
		list.forEach(objArray -> {
			int i = 0;
			final String clientName = (String) objArray[i++];
			final String projectName = (String) objArray[i++];
			final String taskName = (String) objArray[i++];
			final Double hours = (Double) objArray[i++];
			final NetsuiteTaskReportRow row = new NetsuiteTaskReportRow(clientName, projectName,
					taskName, hours);
			resultList.add(row);
		});
		return new ApiResponse<>(HttpStatus.OK.value(), "Success", resultList);
	}
}
