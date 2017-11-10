package com.lankheet.utils;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.pmagent.p1.P1Datagram;

public class JsonUtil {
    private static final Logger logger = LogManager.getLogger(JsonUtil.class);

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static final String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            logger.error("Json Processing was wrong: " + e, logger);
        }

        return null;
    }

    public static P1Datagram fromJson(String json) throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(json, P1Datagram.class);
    }

    public static Measurement measurementFromJson(String json)
            throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(json, Measurement.class);
    }
}
