package org.camoiloc.facts.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(value = "Statistics")
public class Stats {
    @ApiModelProperty(notes = "Total number of random facts retrieved")
    private int total;
    @ApiModelProperty(notes = "Number of unique facts retrieved")
    private int unique;

    public Stats() {

    }

    public Stats(int total, int unique) {
        this.total = total;
        this.unique = unique;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getUnique() {
        return unique;
    }

    public void setUnique(int unique) {
        this.unique = unique;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stats stats = (Stats) o;
        return total == stats.total && unique == stats.unique;
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, unique);
    }
}
