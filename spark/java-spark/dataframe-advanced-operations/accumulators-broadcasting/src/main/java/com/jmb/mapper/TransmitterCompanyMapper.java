package com.jmb.mapper;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Map;

public class TransmitterCompanyMapper extends TransmitterSpeedMapper implements MapFunction<Row, Row> {

    Broadcast<Map<String, String>> lookupTable;

    public TransmitterCompanyMapper(Broadcast<Map<String, String>> lookUpTable) {
        lookupTable = lookUpTable;
    }

    @Override
    public Row call(Row row) {
        int idCol = 0;
        String transId = row.getString(idCol).substring(0, 3);
        String manufacturer = lookupTable.getValue().get(transId);
        return produceRow(transId, manufacturer);
    }

    @Override
    public StructType structType() {
        int colIndex = 0;
        StructField[] columns = new StructField[2];
        columns[colIndex++] = new StructField("Transmitter_id", DataTypes.StringType, true, null);
        columns[colIndex] = new StructField("Manufacturer", DataTypes.StringType, true, null);
        StructType schema = new StructType(columns);
        return schema;
    }
}
