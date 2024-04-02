package com.jmb;

import org.apache.spark.api.java.function.ReduceFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;

public class FarmStandSalesReducer implements ReduceFunction<Row> {

    @Override
    public Row call(Row aggregatedRow, Row currentRow) throws Exception {

        GenericRowWithSchema updatedAggregatedRow = addCurrentRowValues(aggregatedRow, currentRow);
        return updatedAggregatedRow;
    }

    private GenericRowWithSchema addCurrentRowValues(Row aggregatedRow, Row currentRow) {
        Object[] rowValues = new Object[3];
        //Sum Total Sales
        rowValues[0] = ((Double) aggregatedRow.getAs("total_sales"))
                + ((Double) currentRow.getAs("total_sales"));

        rowValues[1] = ((Double) aggregatedRow.getAs("total_snap_sales"))
                + ((Double) currentRow.getAs("total_snap_sales"));

        rowValues[2] = ((Integer)aggregatedRow.getAs("total_double_sales"))
                + ((Integer)currentRow.getAs("total_double_sales"));

        return new GenericRowWithSchema(rowValues, currentRow.schema());
    }


}
