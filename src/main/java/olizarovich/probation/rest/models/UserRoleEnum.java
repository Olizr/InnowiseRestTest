package olizarovich.probation.rest.models;

import org.springframework.security.core.GrantedAuthority;

public enum UserRoleEnum implements GrantedAuthority {
    ADMIN,
    USER,
    ANONYMOUS;

    UserRoleEnum() {
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
