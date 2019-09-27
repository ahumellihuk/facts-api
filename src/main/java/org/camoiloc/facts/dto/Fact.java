package org.camoiloc.facts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(value = "Fact details")
public class Fact implements Cloneable {

    @ApiModelProperty(notes = "Fact ID")
    private String id;

    @ApiModelProperty(notes = "Fact text")
    private String text;

    @ApiModelProperty(notes = "Source of the fact")
    private String source;

    @ApiModelProperty(notes = "Source URL")
    @JsonProperty(value = "source_url")
    private String sourceUrl;

    @ApiModelProperty(notes = "What language the fact is in")
    private String language;

    @ApiModelProperty(notes = "Permanent fact URL", dataType = "string")
    private String permalink;

    public Fact() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Fact fact = (Fact) o;
        return Objects.equals(id, fact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Fact clone() {
        Fact clone = new Fact();
        clone.id = id;
        clone.text = text;
        clone.source = source;
        clone.sourceUrl = sourceUrl;
        clone.language = language;
        clone.permalink = permalink;
        return clone;
    }

}
