package org.rhq.core.domain.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko W. Rupp
 */
public class OperationDefinitionRest {

    public OperationDefinitionRest() {
    }

    String name;
    int id;
    List<Link> links = new ArrayList<Link>();
    List<SimplePropDef> params = new ArrayList<SimplePropDef>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void addLink(Link link) {
        links.add(link);
    }

    public List<SimplePropDef> getParams() {
        return params;
    }

    public void setParams(List<SimplePropDef> params) {
        this.params = params;
    }
}
