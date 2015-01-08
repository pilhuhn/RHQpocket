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
package org.rhq.pocket.test;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import org.rhq.core.domain.rest.ResourceWithType;

/**
 * Test some json stuff
 * @author Heiko W. Rupp
 */
public class JsonTest {

    public static void main(String[] args) {
        JsonTest jt = new JsonTest();
        try{
            jt.testJson1();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            jt.testJson2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testJson1() throws Exception {
        String json = "{\"resourceWithType\":{\"parentId\":0,\"pluginId\":0,\"pluginName\":\"Platforms\",\"resourceId\":10001,\"resourceName\":\"snert\",\"typeId\":10057,\"typeName\":\"Mac OS X\"}}";

        ObjectMapper mapper = new ObjectMapper();

        ResourceWithType rwt = mapper.readValue(json,ResourceWithType.class);
    }

    public void testJson2() throws Exception {
        String json = "{\"resourceWithType\":{\"parentId\":0,\"pluginId\":0,\"pluginName\":\"Platforms\",\"resourceId\":10001,\"resourceName\":\"snert\",\"typeId\":10057,\"typeName\":\"Mac OS X\"}}";

        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(json);
        JsonNode content = root.getElements().next();
        ResourceWithType rwt = mapper.readValue(content,ResourceWithType.class);
    }
}
