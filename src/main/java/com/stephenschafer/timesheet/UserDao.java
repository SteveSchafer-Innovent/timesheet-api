package com.stephenschafer.timesheet;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<UserEntity, Integer> {
	UserEntity findByUsername(String username);
}
