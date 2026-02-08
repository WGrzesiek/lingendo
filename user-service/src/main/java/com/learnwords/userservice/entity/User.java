package com.learnwords.userservice.entity;

import com.learnwords.userservice.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import com.learnwords.userservice.enums.UserType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "user_management")
public class User {
    @Id
    private String id;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    private boolean confirmed = false;

    @Builder.Default
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountType accountType;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = true)
    private Instant lastLogin;

    @Column(nullable = true)
    private Instant lastPasswordChange;

    @Column(nullable = false)
    @Builder.Default
    private int loginCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private int streak = 0;

    @PrePersist
    public void prePersist() {
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public void registerLogin(Instant now) {
        loginCount++;

        if (lastLogin == null) {
            streak = 1;
        } else {
            long diffHours = ChronoUnit.HOURS.between(lastLogin, now);

            if (diffHours < 24) {
            } else if (diffHours < 48) {
                streak++;
            } else {
                streak = 1;
            }
        }
        lastLogin = now;
    }

}
