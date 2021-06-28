package org.example.theblog.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private Timestamp time;

    @Column(columnDefinition = "TINYTEXT NOT NULL")
    private String code;

    @Column(name = "secret_code", columnDefinition = "TINYTEXT NOT NULL")
    private String secretCode;
}
