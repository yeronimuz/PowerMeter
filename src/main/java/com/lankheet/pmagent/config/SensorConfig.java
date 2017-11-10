package com.lankheet.pmagent.config;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lankheet.iot.datatypes.SensorType;

public class SensorConfig {
    @NotNull
    private Integer sensorId;

    @NotEmpty
    private List<SensorType> sensorTypes;

    @JsonProperty
    public Integer getSensorId() {
        return sensorId;
    }

    @JsonProperty
    public List<SensorType> getSensorTypes() {
        return sensorTypes;
    }
}
