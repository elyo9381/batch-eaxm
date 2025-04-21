package com.capybara.springboot.core.api.controller.v1.request;

import com.capybara.springboot.core.domain.ExampleData;

public record ExampleRequestDto(String data) {
    public ExampleData toExampleData() {
        return new ExampleData(data, data);
    }
}
