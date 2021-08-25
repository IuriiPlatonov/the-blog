package org.example.theblog.model.repository;

import org.example.theblog.model.entity.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {

    @Transactional()
    void deleteAllByTimeIsLessThanEqual(LocalDateTime time);

    @Query("SELECT c.code from CaptchaCode c where c.code = :code")
    Optional<String> findCode(@Param("code") String code);

    Optional<CaptchaCode> findCaptchaCodeBySecretCode(String code);
}
