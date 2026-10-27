package com.xpalive.constants;


import lombok.Getter;

public enum ModelName {
    QWEN3_06B("qwen3:0.6b"),
    QWEN3_17B("qwen3:1.7b"),
    ;

    @Getter
    private final String name;

    ModelName(String name) {
        this.name = name;
    }
}
