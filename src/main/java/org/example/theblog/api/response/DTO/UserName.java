package org.example.theblog.api.response.DTO;

import lombok.Data;

@Data
public class UserName {
    private int id;
    private String name;

    public UserName() {
    }

    public UserName(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
