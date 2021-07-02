package org.example.theblog.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table(name = "posts")
@ToString(callSuper = true, of = {"title"})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "is_active", nullable = false)
    private byte isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status",
            columnDefinition = "ENUM('NEW', 'ACCEPTED', 'DECLINED') NOT NULL DEFAULT 'NEW'")
    private ModerationStatus moderationStatus;

    @ManyToOne
    private User moderator;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private Timestamp time;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @OneToMany(mappedBy = "posts")
    private List<TagToPost> tags;

    @OneToMany(mappedBy = "post_id")
    private List<PostVote> postVotes;

    @OneToMany(mappedBy = "post_id")
    private List<PostComment> postComments;
}
