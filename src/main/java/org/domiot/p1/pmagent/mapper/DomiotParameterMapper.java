package org.domiot.p1.pmagent.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.domiot.p1.pmagent.config.ConfigParameter;
import org.lankheet.domiot.domotics.dto.DomiotParameterDto;

public class DomiotParameterMapper {

    private DomiotParameterMapper() {
    }

    static DomiotParameterDto map(ConfigParameter config) {
        DomiotParameterDto dto = new DomiotParameterDto();
        dto.setName(config.getName());
        dto.setValue(config.getValue());
        dto.setParameterType(config.getParameterType());
        dto.setReadonly(config.isReadonly());
        return dto;
    }

    public static List<DomiotParameterDto> mapList(List<ConfigParameter> parameters) {
        return parameters.stream().map(DomiotParameterMapper::map).toList();
    }
}
