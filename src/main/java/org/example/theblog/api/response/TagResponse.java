package org.example.theblog.api.response;

import lombok.Data;
import org.example.theblog.api.response.DTO.TagWeight;

import java.util.Set;

@Data
public class TagResponse {
    Set<TagWeight> tags;
}
