package com.lankheet.pmagent.config;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lankheet.iot.datatypes.entities.SensorType;

public class SensorConfig {
    @NotNull
    private String nic;

    @NotEmpty
    private List<SensorType> sensorTypes;

    @JsonProperty
    public String getNic() {
        return nic;
    }

    @JsonProperty
    public List<SensorType> getSensorTypes() {
        return sensorTypes;
    }
}
