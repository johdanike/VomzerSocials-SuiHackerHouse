package org.vomzersocials.user.data.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.uuid.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.vomzersocials.user.media.models.Media;
import org.vomzersocials.user.utils.Like;
import org.vomzersocials.user.utils.Role;

import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            columnDefinition = "CHAR(36)",
            updatable = false,
            nullable = false
    )
    private String id;

    @Column(name = "user_name")
    private String userName;
    private String password;
    private Boolean isLoggedIn;
    private String suiAddress;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Media> mediaPosts = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Role role;

    // Users Following Others (Many-to-Many Self-Join)
    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "user_id"),           // The owner’s join column
            inverseJoinColumns = @JoinColumn(name = "following_id")  // The join column for the followed user
    )
    private Set<User> following = new HashSet<>();

    // Users Being Followed (Inverse of Following)
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();
    private String publicKey;

    // Likes (One User can Like Many Posts)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();
    private LocalDateTime dateOfCreation;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
        return this.isLoggedIn;
    }



}
