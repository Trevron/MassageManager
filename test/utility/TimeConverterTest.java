package utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import trevron.utility.TimeConverter;

import java.time.*;

/*
    Most of these tests probably won't work if the system time zone is different than America/Los_Angeles.
    Assumes not daylight savings time...
 */

public class TimeConverterTest {

    @Test
    public void testGetLocal() {
        ZonedDateTime utc = ZonedDateTime.of(2020, 01, 01, 8, 00, 00, 00, ZoneId.of("UTC"));
        ZonedDateTime pst = ZonedDateTime.of(2020, 01, 01, 0, 00, 00, 00, ZoneId.of("America/Los_Angeles"));
        Assertions.assertEquals(pst.toLocalDateTime(), TimeConverter.getLocal(utc));
    }

    @Test
    public void testToUTC() {
        LocalDate date = LocalDate.of(2020, 01, 01);
        String hour = "0";
        String minute = "00";

        ZonedDateTime utc = ZonedDateTime.of(2020, 01, 01, 8, 00, 00, 00, ZoneOffset.UTC);

        Assertions.assertEquals(utc, TimeConverter.toUTC(date, hour, minute));
    }

    @Test
    public void testFromUTC() {
        LocalDateTime utc = LocalDateTime.of(2020, 01, 01, 00, 00);

        ZonedDateTime pst = ZonedDateTime.of(2019, 12, 31, 16, 0, 00, 00, ZoneId.of("America/Los_Angeles"));

        Assertions.assertEquals(pst, TimeConverter.fromUTC(utc));
    }


//    @Test
//    public void testGetLocal2() {
//        ZonedDateTime utc = ZonedDateTime.of(2020, 01, 01, 16, 00, 00, 00, ZoneId.of("UTC"));
//        ZonedDateTime pst = ZonedDateTime.of(2020, 01, 01, 8, 00, 00, 00, ZoneId.of("America/Los_Angeles"));
//
//        Assertions.assertEquals(pst.toLocalDateTime(), TimeConverter.getLocal(utc));
//    }
//
//    @Test
//    public void testGetLocal3() {
//        ZonedDateTime utc = ZonedDateTime.of(2020, 01, 01, 16, 24, 05, 00, ZoneId.of("UTC"));
//        ZonedDateTime pst = ZonedDateTime.of(2020, 01, 01, 8, 24, 05, 00, ZoneId.of("America/Los_Angeles"));
//
//        Assertions.assertEquals(pst.toLocalDateTime(), TimeConverter.getLocal(utc));
//    }
//
//
//
//    @Test
//    public void testToUTC2() {
//        LocalDate date = LocalDate.of(2020, 01, 01);
//        String hour = "22";
//        String minute = "00";
//
//        ZonedDateTime utc = ZonedDateTime.of(2020, 01, 02, 6, 00, 00, 00, ZoneOffset.UTC);
//
//        Assertions.assertEquals(utc, TimeConverter.toUTC(date, hour, minute));
//    }
//
//
//
//    @Test
//    public void testFromUTC2() {
//        LocalDateTime utc = LocalDateTime.of(2020, 01, 01, 8, 00);
//        ZonedDateTime utcZDT = ZonedDateTime.of(utc, ZoneOffset.UTC);
//
//        ZonedDateTime pst = ZonedDateTime.of(2020, 01, 01, 0, 0, 00, 00, ZoneId.of("America/Los_Angeles"));
//
//        Assertions.assertEquals(pst, TimeConverter.fromUTC(utc));
//    }

}
