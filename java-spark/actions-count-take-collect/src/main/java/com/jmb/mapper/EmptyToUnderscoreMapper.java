package com.jmb.mapper;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;

public class EmptyToUnderscoreMapper implements MapFunction<Row, Row> {

    private static final String BLANK = " ";
    private static final String UNDERSCORE = "_";
    private String columnName;

    public EmptyToUnderscoreMapper(final String columnName) {
        this.columnName = columnName;
    }

    @Override
    public Row call(Row originalRow) throws Exception {
        //Get row's field names into a list
        String[] fieldNames = originalRow.schema().fieldNames();
        //With fields name (columns names of Schema) and original row, create a new row
        String[] values = populateRowValues(fieldNames, originalRow);
        //Finally create a Spark Type -GenericRowWithSchema- to be returned, preserving the original Schema
        return new GenericRowWithSchema(values, originalRow.schema());
    }

    private String[] populateRowValues(String[] fieldNames, Row originalRow) {
        String[] newValues = new String[fieldNames.length];

        for (int i = 0; i < fieldNames.length; i++) {
            if (fieldNames[i].equals(columnName)) {
                newValues[i] = ((String) originalRow.getAs(columnName))
                        .replace(BLANK, UNDERSCORE);
            } else {
                newValues[i] = (String) originalRow.get(i);
            }
        }
        return newValues;
    }
}
