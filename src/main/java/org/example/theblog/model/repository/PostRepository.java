package org.example.theblog.model.repository;

import org.example.theblog.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'ACCEPTED' and c.time < current_date  " +
           "ORDER BY c.time desc")
    Page<Post> findAllPageOrderByTimeDesc(Pageable pageable);

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'ACCEPTED' and c.time < current_date  " +
           "ORDER BY c.time")
    Page<Post> findAllPageOrderByTime(Pageable pageable);

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'ACCEPTED' and c.time < current_date  " +
           "ORDER BY c.postComments.size desc ")
    Page<Post> findAllPageOrderByCommentDesc(Pageable pageable);

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'ACCEPTED' and c.time < current_date  " +
           "ORDER BY c.postVotes.size desc ")
    Page<Post> findAllPageOrderByVotesDesc(Pageable pageable);
}
