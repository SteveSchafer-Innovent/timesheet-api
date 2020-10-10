package com.stephenschafer.timesheet.report;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportConfig {
	@Bean
	public EventQuery getEventQuery() {
		return new EventQuery();
	}

	@Bean
	public ProjectIdQuery getProjectIdQuery() {
		return new ProjectIdQuery();
	}

	@Bean
	public ReportSummaryQuery getReportSummaryQuery() {
		return new ReportSummaryQuery();
	}

	@Bean
	public RawProjectQuery getRawProjectQuery() {
		return new RawProjectQuery();
	}
}
