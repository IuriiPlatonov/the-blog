package org.example.theblog.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(name = "post_id", nullable = false)
    private int postId;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private byte value;
}
