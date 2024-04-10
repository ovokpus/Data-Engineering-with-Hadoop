package com.jmb.mapper;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.StructType;

public abstract class TransmitterMapperBase {

    protected static final String ACCEPTABLE = "ACPT";
    protected static final String CONGESTED = "CGTD";
    protected static final String OVERLOADED = "OVLD";
    protected static final String ERR_VAL = "ERR";

    protected Row produceRow(String val1, String val2) {
        //Populate Row's values
        int rowIndex = 0;
        Object[] values = new Object[2];
        values[rowIndex++] = val1;
        values[rowIndex] = val2;

        //Define the Row (DataFrame) schema:
        return new GenericRowWithSchema(values, structType());
    }

    protected abstract StructType structType();
}
