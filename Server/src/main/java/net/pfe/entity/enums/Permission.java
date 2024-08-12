package net.pfe.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    CHEF_READ("chef:read"),
    CHEF_UPDATE("chef:update"),
    CHEF_CREATE("chef:create"),
    CHEF_DELETE("chef:delete"),
    COLLABORATEUR_READ("collaborateur:read"),
    COLLABORATEUR_UPDATE("collaborateur:update"),
    COLLABORATEUR_CREATE("collaborateur:create"),
    COLLABORATEUR_DELETE("collaborateur:delete"),




    ;
    @Getter
    private final String permission;
}
