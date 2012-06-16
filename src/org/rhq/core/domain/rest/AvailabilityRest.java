/*
 * RHQ Management Platform
 * Copyright (C) 2005-2011 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.core.domain.rest;

/**
 * Availability implementation for exposing via REST
 * @author Heiko W. Rupp
 */
public class AvailabilityRest {

    long since;
    String type;
    Long until;

    int resourceId;

    public AvailabilityRest() {
    }

    public long getSince() {
        return since;
    }

    public String getType() {
        return type;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setSince(long since) {
        this.since = since;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public Long getUntil() {
        return until;
    }

    public void setUntil(Long until) {
        this.until = until;
    }
}
