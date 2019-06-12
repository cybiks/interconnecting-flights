package org.cybiks.interconnecting.timezone;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConfigurationProperties
public class TimeZoneIATAMapperPropertyBased implements TimeZoneIATAMapper {
    private Map<String, String> timezoneMapper;

    public Map<String, String> getTimezoneMapper() {
        return timezoneMapper;
    }

    public void setTimezoneMapper(Map<String, String> timezoneMapper) {
        this.timezoneMapper = timezoneMapper;
    }

    @Override
    public String findTimeZoneID(String airport) {
        return timezoneMapper.get(airport);
    }
}
