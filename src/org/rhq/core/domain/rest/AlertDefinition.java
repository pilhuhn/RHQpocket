package org.rhq.core.domain.rest;

/**
 * Alert Definition
 * @author Heiko W. Rupp
 */
public class AlertDefinition {

    int id;
    String name;
    boolean enabled;
    String priority;

    public AlertDefinition() {
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
