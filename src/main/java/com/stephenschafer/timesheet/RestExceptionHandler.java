package com.stephenschafer.timesheet;

import java.text.ParseException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unknown error")
	public ApiResponse<Void> handleNotFoundException(final RuntimeException ex) {
		log.info("Exception: " + ex);
		final ApiResponse<Void> apiResponse = new ApiResponse<>(400, "Unknown error", null);
		return apiResponse;
	}

	@ExceptionHandler(MultipartException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Multipart exception")
	public ApiResponse<Void> handleError1(final MultipartException e,
			final RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
		log.info("Exception: " + e);
		final ApiResponse<Void> apiResponse = new ApiResponse<>(400, "Multipart Exception", null);
		return apiResponse;
	}

	@ExceptionHandler(ParseException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad date format")
	public ApiResponse<Void> handleParseException(final ParseException ex) {
		log.info("Exception: " + ex);
		final ApiResponse<Void> apiResponse = new ApiResponse<>(400, "Bad date format", null);
		return apiResponse;
	}
}
