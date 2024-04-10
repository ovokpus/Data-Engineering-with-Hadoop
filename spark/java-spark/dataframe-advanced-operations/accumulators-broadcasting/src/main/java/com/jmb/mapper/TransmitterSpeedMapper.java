package com.jmb.mapper;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class TransmitterSpeedMapper extends TransmitterMapperBase implements MapFunction<Row, Row> {

    private int sourceColId = 0;
    private int pingColId = 2;

    @Override
    public Row call(Row row) throws Exception {

        //Code to deal with erroneous rows
        if(row.anyNull()) {
            return produceRow(ERR_VAL, ERR_VAL);
        }

        //Get ping, analyse and produce new Row
        int rowPing = row.getInt(pingColId);
        String transId = row.getString(sourceColId);
        String category = getCategory(rowPing);
        return produceRow(transId, category);
    }

    private String getCategory(int ping) {
        if(ping <= 50) {
            return ACCEPTABLE;
        } else if(ping > 50 && ping <= 100) {
            return CONGESTED;
        } else {
            return OVERLOADED;
        }
    }

    @Override
    public StructType structType() {
        int colIndex = 0;
        StructField[] columns = new StructField[2];
        columns[colIndex++] = new StructField("Transmitter_id", DataTypes.StringType, true, null);
        columns[colIndex] = new StructField("Category", DataTypes.StringType, true, null);
        StructType schema = new StructType(columns);
        return schema;
    }
}
