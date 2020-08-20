package trevron.utility;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeConverter {

    public static ZonedDateTime toUTC(LocalDate date, String hour, String minute) {
        // Get local date times
        LocalDateTime ldt = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), Integer.parseInt(hour), Integer.parseInt(minute));

        // Get the zoned date time version of local date time.
        ZonedDateTime locZDT = ZonedDateTime.of(ldt, ZoneId.systemDefault());

        // Get the UTC zoned date time. This is what we will submit to the database
        ZonedDateTime utcZDT = locZDT.withZoneSameInstant(ZoneOffset.UTC);

        return utcZDT;
    }

    public static ZonedDateTime toUTC(LocalDateTime ldt) {
        // Get the zoned date time version of local date time.
        ZonedDateTime locZDT = ZonedDateTime.of(ldt, ZoneId.systemDefault());

        // Get the UTC zoned date time. This is what we will submit to the database
        ZonedDateTime utcZDT = locZDT.withZoneSameInstant(ZoneOffset.UTC);

        return utcZDT;
    }

    public static ZonedDateTime fromUTC(LocalDateTime utc) {
        ZonedDateTime utcZDT = ZonedDateTime.of(utc, ZoneOffset.UTC);

        ZonedDateTime locZDT = utcZDT.withZoneSameInstant(ZoneOffset.systemDefault());

        return locZDT;
    }

    public static String format(LocalDateTime ldt) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(ldt);
    }

    public static String format(ZonedDateTime zdt) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(zdt);
    }
}
