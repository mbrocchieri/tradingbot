package org.tradingbot.common.persistence.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

import static org.tradingbot.common.Constants.UTC;

@Converter
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        return Timestamp.from(zonedDateTime.withZoneSameInstant(UTC).toInstant());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp timestamp) {
        return ZonedDateTime.of(timestamp.toLocalDateTime(), UTC).withZoneSameInstant(UTC);
    }
}
