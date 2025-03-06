package com.sid.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity  // ✅ Required for JPA to recognize this as an entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")  // Ensure the table name matches your database schema
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    private String name;
    private String email;
    private String password;
    private String role;

}