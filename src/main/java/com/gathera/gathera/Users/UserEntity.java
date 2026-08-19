package com.gathera.gathera.Users;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String password;

    private String role;

    private Integer userAge;

    public UserEntity() {
    }

    public UserEntity(Long id, String login, String password, Integer userAge, String role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.userAge = userAge;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer age) {
        this.userAge = age;
    }
}
