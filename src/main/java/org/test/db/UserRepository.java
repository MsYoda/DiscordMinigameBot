package org.test.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.test.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
