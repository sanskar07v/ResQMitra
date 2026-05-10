package com.resqmitra.module.user.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class UserIdSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (user != null) {
            gen.writeString(user.getEmail()); // only serialize the email
        } else {
            gen.writeNull();
        }
    }
}

