package org.example.theblog.model.repository;

import org.example.theblog.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    @Query("select c from Tag c")
    List<Tag> findAllTags();

    @Query("select function('MAX', function('size' ,c.posts)) from Tag c")
    Integer findMaxPostsCountInTags();

    Optional<Tag> findTagByName(String name);
}
