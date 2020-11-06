package com.stephenschafer.timesheet.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.stephenschafer.timesheet.ApiResponse;
import com.stephenschafer.timesheet.UserEntity;
import com.stephenschafer.timesheet.UserService;

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

	@GetMapping("/week/{dateString}")
	@ResponseBody
	public ApiResponse<List<ReportSummaryRow>> getRows(
			@PathVariable(required = true) final String dateString,
			final HttpServletRequest request) throws ParseException {
		log.info("GET /report startDate=" + dateString);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final Date day = df.parse(dateString);
		final ReportSummaryQuery query = reportSummaryQuery;
		final Stream<ReportSummaryRow> stream = query.getStream(day, user.getId());
		final List<ReportSummaryRow> list = stream.collect(Collectors.toList());
		return new ApiResponse<>(HttpStatus.OK.value(), "Success", list);
	}
}
