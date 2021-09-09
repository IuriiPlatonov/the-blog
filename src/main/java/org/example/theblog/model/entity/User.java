package org.example.theblog.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.theblog.config.Role;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_moderator", nullable = false)
    private byte isModerator;

    @Column(name = "reg_time", nullable = false)
    private LocalDateTime regTime;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String code;

    @Column(columnDefinition = "TEXT")
    private String photo;

    public Role getRole() {
        return isModerator == 0 ? Role.USER : Role.MODERATOR;
    }
}
