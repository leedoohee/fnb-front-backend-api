package com.fnb.front.backend.controller.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PageResponse<T> {
    private int lastPage;
    private List<T> data;
}
