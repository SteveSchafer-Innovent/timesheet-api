package com.stephenschafer.timesheet;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;

	@PostMapping
	public ApiResponse<UserEntity> saveUser(@RequestBody final UserDto user) {
		return new ApiResponse<>(HttpStatus.OK.value(), "User saved successfully.",
				userService.save(user));
	}

	@GetMapping
	public ApiResponse<List<UserEntity>> listUser() {
		return new ApiResponse<>(HttpStatus.OK.value(), "User list fetched successfully.",
				userService.findAll());
	}

	@GetMapping("/{id}")
	public ApiResponse<UserEntity> getOne(@PathVariable final int id) {
		return new ApiResponse<>(HttpStatus.OK.value(), "User fetched successfully.",
				userService.findById(id));
	}

	@PutMapping("/{id}")
	public ApiResponse<UserDto> update(@RequestBody final UserDto userDto) {
		return new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully.",
				userService.update(userDto));
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> delete(@PathVariable final int id) {
		userService.delete(id);
		return new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully.", null);
	}
}
