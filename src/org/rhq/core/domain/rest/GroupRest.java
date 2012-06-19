package org.rhq.core.domain.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a resource group
 * @author Heiko W. Rupp
 */
public class GroupRest {


    int id;
    private String name;
    private Integer resourceTypeId;
    private boolean recursive;
    private GroupCategory category;

    List<Link> links = new ArrayList<Link>();

    public GroupRest() {
    }

    public GroupRest(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(Integer resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public GroupCategory getCategory() {
        return category;
    }

    public void setCategory(GroupCategory category) {
        this.category = category;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
