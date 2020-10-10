package com.stephenschafer.timesheet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
	private int status;
	private String message;
	private T result;
}
