package com.stephenschafer.timesheet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
	private int id;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
}
