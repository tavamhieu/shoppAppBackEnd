package com.example.shopapppro.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "categories")
@Data//lây phương thuwecs toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tự tăng id khi thêm bản ghi mới (không có bản ghi nào dc phép giống nhau)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

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
}