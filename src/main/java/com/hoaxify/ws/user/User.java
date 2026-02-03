package com.hoaxify.ws.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoaxify.ws.auth.token.Token;

import jakarta.persistence.*;

@Entity
@Table(name="users", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String username;

    String email;

    @JsonIgnore
    String password;

    @JsonIgnore
    boolean active = false;

    @JsonIgnore
    String activationToken;

    @Lob
    String image;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnore
    List<Token> tokens;

    String passwordResetToken;

    String role = "USER";

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getActivationToken() { return activationToken; }
    public void setActivationToken(String activationToken) { this.activationToken = activationToken; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public List<Token> getTokens() { return tokens; }
    public void setTokens(List<Token> tokens) { this.tokens = tokens; }
    public String getPasswordResetToken() { return passwordResetToken; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}