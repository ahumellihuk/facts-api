package org.camoiloc.facts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Service status")
public class StatusResponse {

    @ApiModelProperty(notes = "Status value")
    @JsonProperty
    private Status status;

    @ApiModelProperty(notes = "Facts statistics")
    @JsonProperty("facts")
    private Stats stats;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }
}
