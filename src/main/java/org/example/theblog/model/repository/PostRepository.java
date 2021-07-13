package org.example.theblog.model.repository;

import org.example.theblog.api.response.PostDateCountResponse;
import org.example.theblog.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Override
    long count();

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
           "ORDER BY function('SIZE', c.postComments) desc ")
    Page<Post> findAllPageOrderByCommentDesc(Pageable pageable);

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'ACCEPTED' and c.time < current_date  " +
           "ORDER BY function('SIZE', c.postVotes) desc ")
    Page<Post> findAllPageOrderByVotesDesc(Pageable pageable);

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'ACCEPTED' and c.time < current_date " +
           "and (c.text like %:text%  or c.title like %:text%)" +
           "ORDER BY c.time desc")
    Page<Post> searchPageByQuery(@Param("text") String text, Pageable pageable);

    @Query(value = "select YEAR(time) as year from posts c GROUP BY year",
            nativeQuery = true)
    Set<Integer> getYearsList();

    @Query("SELECT new org.example.theblog.api.response.PostDateCountResponse(" +
           "function('date_format', c.time, '%Y-%m-%d')," +
           "function('count', function('date_format', c.time, '%Y-%m-%d')))" +
           "FROM Post c " +
           "WHERE function('date_format', c.time, '%Y') = :year " +
           "GROUP BY function('date_format', c.time, '%Y-%m-%d')")
    List<PostDateCountResponse> getYearsListList(@Param("year") String year);

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'ACCEPTED' and c.time < current_date " +
           "and function('date_format', c.time, '%Y-%m-%d') = :date " +
           "ORDER BY function('SIZE', c.postVotes) desc ")
    Page<Post> findAllPostsByDate(@Param("date") String date, Pageable pageable);

    @Query("select c from Post c " +
           "Join c.tags t " +
           "where c.isActive = 1 and c.moderationStatus = 'ACCEPTED' and c.time < current_date " +
           "and t.name = :tag " +
           "ORDER BY function('SIZE', c.postVotes) desc ")
    Page<Post> findAllPostsByTag(@Param("tag") String tag, Pageable pageable);

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'ACCEPTED' and c.time < current_date  " +
           "and c.id = :id")
    Post getPostById(@Param("id") int id);

    @Query("select c from Post c " +
           "where c.isActive = 0 and c.user.email = :email " +
           "ORDER BY c.time desc")
    Page<Post> findMyInactivePosts(@Param("email") String email, Pageable pageable);

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'NEW' and c.user.email = :email " +
           "ORDER BY c.time desc")
    Page<Post> findMyPendingPosts(@Param("email") String email, Pageable pageable);

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'DECLINED' and c.user.email = :email " +
           "ORDER BY c.time desc")
    Page<Post> findMyDeclinedPosts(@Param("email") String email, Pageable pageable);

    @Query("select c from Post c " +
           "where c.isActive = 1 and c.moderationStatus = 'ACCEPTED' and c.user.email = :email " +
           "ORDER BY c.time desc")
    Page<Post> findMyPublishedPosts(@Param("email") String email, Pageable pageable);
}

