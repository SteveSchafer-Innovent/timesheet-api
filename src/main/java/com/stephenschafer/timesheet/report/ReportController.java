package com.stephenschafer.timesheet.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.stephenschafer.timesheet.ApiResponse;
import com.stephenschafer.timesheet.UserEntity;
import com.stephenschafer.timesheet.UserService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/report")
public class ReportController {
	@Autowired
	UserService userService;
	@Autowired
	ReportSummaryQuery reportSummaryQuery;
	@Autowired
	ReportDetailQuery reportDetailQuery;
	@Autowired
	ProjectDateDao projectDateDao;

	@GetMapping("/week/{dateString}")
	@ResponseBody
	public ApiResponse<List<ReportSummaryRow>> getRows(
			@PathVariable(required = true) final String dateString,
			final HttpServletRequest request) throws ParseException {
		log.info("GET /report/week/" + dateString);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final Date day = df.parse(dateString);
		final ReportSummaryQuery query = reportSummaryQuery;
		final Stream<ReportSummaryRow> stream = query.getStream(day, user.getId());
		final List<ReportSummaryRow> list = stream.collect(Collectors.toList());
		return new ApiResponse<>(HttpStatus.OK.value(), "Success", list);
	}

	@GetMapping("/detail/{startDateString}/{endDateString}")
	@ResponseBody
	public ApiResponse<List<ReportDetailRow>> getDetailRows(
			@PathVariable(required = true) final String startDateString,
			@PathVariable(required = true) final String endDateString,
			final HttpServletRequest request) throws ParseException {
		log.info("GET /report/detail/" + startDateString + "/" + endDateString);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final Date startDate = "none".equalsIgnoreCase(startDateString) ? null
			: df.parse(startDateString);
		final Date endDate = "none".equalsIgnoreCase(endDateString) ? null
			: df.parse(endDateString);
		final Stream<ReportDetailRow> stream = reportDetailQuery.getStream(startDate, endDate,
			user.getId());
		final List<ReportDetailRow> list = stream.collect(Collectors.toList());
		return new ApiResponse<>(HttpStatus.OK.value(), "Success", list);
	}

	@PostMapping("/check")
	@ResponseBody
	public ApiResponse<Void> check(@RequestBody final ProjectDateEntity projectDateEntity,
			final HttpServletRequest request) {
		log.info("POST /report/check " + projectDateEntity);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		projectDateEntity.setUserId(user.getId());
		projectDateDao.save(projectDateEntity);
		return new ApiResponse<>(HttpStatus.OK.value(), "Success", null);
	}

	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	private static class CheckResponse {
		private long time;
		private boolean checked;
	}

	@GetMapping("/check/{projectId}/{dateString}")
	@ResponseBody
	public ApiResponse<CheckResponse> getCheck(@PathVariable final int projectId,
			@PathVariable final String dateString, final HttpServletRequest request)
			throws ParseException {
		log.info("GET /report/check/" + projectId + "/" + dateString);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		final Optional<ProjectDateEntity> optionalProjectDate = projectDateDao.findById(
			new ProjectDateKey(user.getId(), projectId, dateString));
		if (optionalProjectDate.isPresent()) {
			final ProjectDateEntity projectDate = optionalProjectDate.get();
			final CheckResponse checkResponse = new CheckResponse(projectDate.getTime(),
					projectDate.isChecked());
			log.info("checkResponse = " + checkResponse);
			return new ApiResponse<>(HttpStatus.OK.value(), "Success", checkResponse);
		}
		log.info(projectId + "/" + dateString + " not found");
		return new ApiResponse<>(HttpStatus.OK.value(), "Not found", new CheckResponse(0L, false));
	}
}
