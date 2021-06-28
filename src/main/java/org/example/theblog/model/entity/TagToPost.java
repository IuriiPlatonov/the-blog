package org.example.theblog.model.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tag2post")
public class TagToPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post posts;

    @ManyToOne()
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tags;
}
