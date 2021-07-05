package org.example.theblog.model.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class OffsetLimitPageable extends PageRequest {
    private int offset;

    public OffsetLimitPageable(int offset, int limit){
        super(offset,limit, Sort.unsorted());
        this.offset=offset;
    }
    @Override
    public long getOffset(){
        return this.offset;
    }
}