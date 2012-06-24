package org.rhq.pocket.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import org.rhq.core.domain.rest.OperationRest;
import org.rhq.pocket.RHQPocket;

/**
 * Serializer class that hacks around an API change in OperationRest
 * @author Heiko W. Rupp
 */
public class OperationRestSerializer extends JsonSerializer<OperationRest> {

    @Override
    public void serialize(OperationRest operationRest, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name",operationRest.getName());
        jsonGenerator.writeNumberField("id",operationRest.getId());
        jsonGenerator.writeNumberField("resourceId",operationRest.getResourceId());
        jsonGenerator.writeNumberField("definitionId",operationRest.getDefinitionId());
        if (RHQPocket.is44()) {
            jsonGenerator.writeStringField("state","ready");
        } else {
            jsonGenerator.writeBooleanField("readyToSubmit",operationRest.isReadyToSubmit());
        }
        jsonGenerator.writeObjectField("params",operationRest.getParams());
        jsonGenerator.writeEndObject();
    }
}
