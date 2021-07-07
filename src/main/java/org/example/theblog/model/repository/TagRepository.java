package org.example.theblog.model.repository;

import org.example.theblog.model.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {
    @Query("select c from Tag c")
    List<Tag> findAllTags();

    @Query("select function('MAX', function('size' ,c.posts)) from Tag c")
    Integer findMaxPostsCountInTags();
}
