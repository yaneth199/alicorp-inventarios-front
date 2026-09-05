package pe.edu.utp.sigpi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String email;
    private String role;
    private boolean active = true;
    private LocalDateTime lastAccess;

    public AppUser() {}
    public AppUser(String fullName, String username, String password, String email, String role, boolean active) {
        this.fullName=fullName; this.username=username; this.password=password; this.email=email; this.role=role; this.active=active;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getLastAccess() { return lastAccess; }
    public void setLastAccess(LocalDateTime lastAccess) { this.lastAccess = lastAccess; }
    @Transient public String getInitials() {
        if (fullName == null || fullName.isBlank()) return "U";
        String[] p=fullName.trim().split("\\s+");
        return (p[0].substring(0,1) + (p.length>1?p[p.length-1].substring(0,1):"")).toUpperCase();
    }
}
