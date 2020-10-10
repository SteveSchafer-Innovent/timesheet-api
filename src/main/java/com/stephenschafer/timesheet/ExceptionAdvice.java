package com.stephenschafer.timesheet;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
	@ExceptionHandler(RuntimeException.class)
	public ApiResponse<Void> handleNotFoundException(final RuntimeException ex) {
		final ApiResponse<Void> apiResponse = new ApiResponse<>(400, "Bad request", null);
		return apiResponse;
	}
}
