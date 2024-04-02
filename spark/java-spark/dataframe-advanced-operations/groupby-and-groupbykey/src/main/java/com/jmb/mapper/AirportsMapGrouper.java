package com.jmb.mapper;

import org.apache.spark.api.java.function.MapGroupsFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Iterator;
import java.util.Objects;

/**
 * Mapper works on grouped Airports information, Airport.Code being the key,
 * and returns a row with modified data. In this example, a Row with percentages per month and per airport.
 *
 * A MapGroupsFunction interface works on grouped data per partition based on a key.
 */
public class AirportsMapGrouper implements MapGroupsFunction<String, Row, Row> {

    private static final int MONTHS = 12;
    private static final int AIRPORT_INDEX = 0;

    @Override
    public Row call(String key, Iterator<Row> iterator) throws Exception {
        int flightsFieldIndex = 4, monthFieldIndex = 3, total = 0;
        int[] partialResults = new int[MONTHS];

        //Values for the months flights plus the corresponding airport code.
        Object[] rowValues = new Object[MONTHS + 1];
        String airportCode = null;

       //Add the partial sums of flights per month
        while(iterator.hasNext()) {
            Row current = iterator.next();

            if(Objects.isNull(airportCode)) {
                airportCode = (String) current.get(AIRPORT_INDEX);
            }

            int monthFlights = current.getInt(flightsFieldIndex);
            total += monthFlights;
            partialResults[current.getInt(monthFieldIndex) - 1] += monthFlights;
        }

        //Calculate percentages and return the row holding that information
        rowValues[AIRPORT_INDEX] = airportCode;
        for(int i = 1, j = 0; i < MONTHS + 1; i++, j++) {
            rowValues[i] = (partialResults[j] * 100.0f) / total;
        }

        return new GenericRowWithSchema(rowValues, new StructType(defineRowSchema()));
    }

    public static StructField[] defineRowSchema() {
        StructField[] fields = new StructField[MONTHS + 1];
        //Add the first column name as the Airport Code
        fields[AIRPORT_INDEX] = new StructField("Airport Code", DataTypes.StringType, true, Metadata.empty());
        //Add Months columns after the first one which is airport name
        for(int i = 1; i < MONTHS + 1; i++) {
            fields[i] = new StructField(String.valueOf(i), DataTypes.FloatType, true, Metadata.empty());
        }
        return fields;
    }
}
