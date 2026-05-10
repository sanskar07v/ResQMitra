package com.resqmitra.module.incident.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class IncidentIdSerializer extends JsonSerializer<Incident> {
    @Override
    public void serialize(Incident incident, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (incident != null && incident.getIncidentId() != null) {
            gen.writeNumber(incident.getIncidentId());
        } else {
            gen.writeNull();
        }
    }
}
