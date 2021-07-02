package org.example.theblog.api.response.DTO;

import lombok.Data;

@Data
public class TagWeight {
    private String name;
    private double weight;

    public TagWeight(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }
}
