package com.jmb.mapper;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Map;

public class NationalityMapper implements MapFunction<Row, Row> {

    private Map<String, String> nationsDict;

    public NationalityMapper(Map<String, String> nationsDict) {
        this.nationsDict = nationsDict;
    }

    @Override
    public Row call(Row row) throws Exception {
        int colIndex = 0, natColIndex = 2;
        Object[] rowValues = new Object[row.schema().length() + 1];
        rowValues[colIndex] = row.get(colIndex++);
        rowValues[colIndex] = row.get(colIndex);
        //Assign Nationality
        rowValues[natColIndex] = nationsDict.get(row.get(colIndex));
        return new GenericRowWithSchema(rowValues, getSchema());
    }

    public static StructType getSchema() {
        StructField[] fields = new StructField[]{
                new StructField("First_Name", DataTypes.StringType, true, null),
                new StructField("Last_name", DataTypes.StringType, true, null),
                new StructField("Nationality", DataTypes.StringType, true, null)
        };
        return new StructType(fields);
    }
}
