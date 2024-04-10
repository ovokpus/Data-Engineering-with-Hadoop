package com.jmb.udfs;

import org.apache.spark.sql.api.java.UDF2;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.Date;

public class DateParserUDF implements UDF2<String, String, Date> {

    @Override
    public Date call(String dateString, String dateFormat) throws Exception {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormat);
        DateTime parsedDate = formatter.parseDateTime(dateString);
        return new java.sql.Date(parsedDate.getMillis());
    }
}
