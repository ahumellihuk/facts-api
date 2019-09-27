package org.camoiloc.facts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class SupportedDirectionsResponse {

    @JsonProperty("dirs")
    private Set<String> directions;

    public SupportedDirectionsResponse() {
    }

    public Set<String> getDirections() {
        return directions;
    }

    public void setDirections(Set<String> directions) {
        this.directions = directions;
    }
}
