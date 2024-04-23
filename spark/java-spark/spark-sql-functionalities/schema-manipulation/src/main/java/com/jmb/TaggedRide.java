package com.jmb;

import java.io.Serializable;

/**
 * Class is a POJO that represents a specific business domain object for rides with tags.
 */
public class TaggedRide implements Serializable {

    private String name;
    private String[] tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
