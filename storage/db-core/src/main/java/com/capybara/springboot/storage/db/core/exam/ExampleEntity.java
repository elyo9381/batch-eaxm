package com.capybara.springboot.storage.db.core.exam;

import com.capybara.springboot.storage.db.core.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ExampleEntity extends BaseEntity {

    @Id
    @Tsid
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    private String exampleColumn;


    public ExampleEntity() {
    }

    public ExampleEntity(String exampleColumn) {
        this.exampleColumn = exampleColumn;
    }

    public String getExampleColumn() {
        return exampleColumn;
    }

}
