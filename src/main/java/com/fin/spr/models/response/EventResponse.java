package com.fin.spr.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class EventResponse {

    @JsonProperty("is_free")
    private boolean free;

    @JsonProperty("dates")
    private List<DataResponse> dates;

    @JsonProperty("title")
    private String title;

    @JsonProperty("price")
    private String price;

    @JsonProperty("location")
    private LocationResponse location;
}
