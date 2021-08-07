package org.example.theblog.model.repository;

import org.example.theblog.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    User findUsersByEmail(String email);
    User findUsersByCode(String code);
}
