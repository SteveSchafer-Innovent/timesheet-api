package com.stephenschafer.timesheet;

import java.util.List;

public interface UserService {
	UserEntity save(UserDto user);

	List<UserEntity> findAll();

	void delete(int id);

	UserEntity findByUsername(String username);

	UserEntity findById(int id);

	UserDto update(UserDto userDto);
}
