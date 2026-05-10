package com.resqmitra.utilities;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Role {
	
    @JsonProperty("Volunteer")
    ROLE_VOLUNTEER("Volunteer"),
    
    @JsonProperty("Admin")
    ROLE_ADMIN("Admin"),
    
    @JsonProperty("Citizen")
    ROLE_CITIZEN("Citizen");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
