package com.example.demoauth.DTO;

public class CategoryDTO {
    private Long id;
    private String name;
    private Long categoryCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Long getCategoryCode() {
        return categoryCode;
    }
    public void setCategoryCode(Long categoryCode) {
        this.categoryCode = categoryCode;
    }

}
