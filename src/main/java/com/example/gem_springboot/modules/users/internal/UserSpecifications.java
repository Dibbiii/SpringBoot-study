package com.example.gem_springboot.modules.users.internal;

import com.example.gem_springboot.modules.users.UserFilter;
import org.springframework.data.jpa.domain.Specification;

// Traduce i filtri in SQL
public class UserSpecifications {

    public static Specification<UserEntity> withFilter(UserFilter filter) {
        return Specification.where(hasUsername(filter.username()))
            .and(hasEmail(filter.email()))
            .and(createdAfter(filter.createdAfter()))
            .and(createdBefore(filter.createdBefore()));
    }

    private static Specification<UserEntity> hasUsername(String username) {
        return (root, query, cb) -> {
            if (username == null || username.isEmpty()) return null;
            return cb.like(
                cb.lower(root.get("username")),
                "%" + username.toLowerCase() + "%"
            );
        };
    }

    private static Specification<UserEntity> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isEmpty()) return null;
            return cb.like(
                cb.lower(root.get("email")),
                "%" + email.toLowerCase() + "%"
            );
        };
    }

    private static Specification<UserEntity> createdAfter(
        java.time.LocalDateTime date
    ) {
        return (root, query, cb) -> {
            if (date == null) return null;
            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    private static Specification<UserEntity> createdBefore(
        java.time.LocalDateTime date
    ) {
        return (root, query, cb) -> {
            if (date == null) return null;
            return cb.lessThanOrEqualTo(root.get("createdAt"), date);
        };
    }
}
