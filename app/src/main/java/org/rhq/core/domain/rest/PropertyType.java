package org.rhq.core.domain.rest;

/**
 * Type of a property. Corresponds to PropertySimpleType on the server
 * @author Heiko W. Rupp
 */
public enum PropertyType {

    BOOLEAN,
    STRING,
    LONG_STRING,
    INTEGER,
    FLOAT,
    LONG,
    DOUBLE,
    FILE,
    DIRECTORY,
    PASSWORD;

    public static boolean isNumeric(PropertyType type) {
        return type == INTEGER || type==LONG || type==FLOAT || type==DOUBLE;
    }
}
