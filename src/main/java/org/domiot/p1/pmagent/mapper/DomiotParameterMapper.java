package org.domiot.p1.pmagent.mapper;

import java.util.ArrayList;
import java.util.List;

import org.domiot.p1.pmagent.config.ConfigParameter;
import org.lankheet.domiot.domotics.dto.DomiotParameterDto;

/**
 * Mapper class for Domiot Parameter configuration.
 */
public class DomiotParameterMapper {

    private DomiotParameterMapper() {
    }

    static DomiotParameterDto map(ConfigParameter config) {
        DomiotParameterDto dto = new DomiotParameterDto();
        dto.setName(config.getName());
        dto.setValue(config.getValue());
        dto.setParameterType(config.getType());
        dto.setReadonly(config.isReadonly());
        return dto;
    }

    /**
     * Map configuration to a DTO list
     *
     * @param parameters List of config parameters
     * @return Converted list or empty list when parameters is null
     */
    static List<DomiotParameterDto> mapList(List<ConfigParameter> parameters) {
        return (parameters != null) ? parameters.stream().map(DomiotParameterMapper::map).toList() : new ArrayList<>();
    }
}
