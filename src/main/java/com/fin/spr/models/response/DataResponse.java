package com.fin.spr.models.response;

import lombok.Getter;

import java.time.Instant;

@Getter
public class DataResponse {
    private Instant start;
    private Instant end;
}
