package org.cybiks.interconnecting.timezone;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TimeZoneIATAMapperTest {
    @Autowired
    private TimeZoneIATAMapper timeZoneIATAMapper;

    @Test
    public void testMapLoad() {
        Assert.assertEquals("Pacific/Tahiti", timeZoneIATAMapper.findTimeZoneID("AAA"));
    }

}
