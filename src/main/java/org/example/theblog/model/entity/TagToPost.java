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

    private int post_id;

    private int tag_id;
}
