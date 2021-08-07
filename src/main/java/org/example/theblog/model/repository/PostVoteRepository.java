package org.example.theblog.model.repository;

import org.example.theblog.model.entity.PostVote;
import org.example.theblog.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {

    Optional<PostVote> findByPostIdAndUser(int postId, User user);
}
