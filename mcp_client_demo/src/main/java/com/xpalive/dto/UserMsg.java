package com.xpalive.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserMsg(
        @JsonProperty("message") String message
) {
}
