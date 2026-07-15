package com.shaurya.spring.expensetracker.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authority;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}