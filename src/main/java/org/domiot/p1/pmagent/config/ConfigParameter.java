package org.domiot.p1.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ConfigParameter {
    @JsonProperty
    private String name;
    @JsonProperty
    private String parameterType;
    @JsonProperty
    private Object value;
    @JsonProperty
    private boolean readonly;

}
