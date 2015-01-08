
package org.rhq.core.domain.rest;

/**
 * One single property definition
 * @author Heiko W. Rupp
 */
public class SimplePropDef {

    String name;
    boolean required;
    PropertyType type;
    private String defaultValue;

    public SimplePropDef() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
