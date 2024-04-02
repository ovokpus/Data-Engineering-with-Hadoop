package com.jmb.filter;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Row;

public class HerbsFilter implements FilterFunction<Row> {

    private static final String SUB_GROUP_COLUMN = "SUB GROUP";

    @Override
    public boolean call(Row row) throws Exception {
        return row.getAs(SUB_GROUP_COLUMN).equals("Herbs");
    }
}
