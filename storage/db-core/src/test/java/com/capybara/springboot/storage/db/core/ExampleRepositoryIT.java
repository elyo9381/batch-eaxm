package com.capybara.springboot.storage.db.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.capybara.springboot.storage.db.CoreDbContextTest;

import com.capybara.springboot.storage.db.core.exam.ExampleEntity;
import com.capybara.springboot.storage.db.core.exam.ExampleRepository;
import org.junit.jupiter.api.Test;

public class ExampleRepositoryIT extends CoreDbContextTest {

    private final ExampleRepository exampleRepository;

    public ExampleRepositoryIT(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    @Test
    public void testShouldBeSavedAndFound() {
        ExampleEntity saved = exampleRepository.save(new ExampleEntity("SPRING_BOOT"));
        assertThat(saved.getExampleColumn()).isEqualTo("SPRING_BOOT");

        ExampleEntity found = exampleRepository.findById(saved.getId()).get();
        assertThat(found.getExampleColumn()).isEqualTo("SPRING_BOOT");
    }
}
