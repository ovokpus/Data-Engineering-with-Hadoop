package com.jmb.mapper;

import com.jmb.Estimator;
import com.jmb.model.DamageEstimator;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Map;

public class DamageEstMapper implements MapFunction<Row, Row> {

    private Estimator estimator;

    public DamageEstMapper(Estimator estimator) {
        this.estimator = estimator;
    }

    @Override
    public Row call(Row row) throws Exception {
        //Row indices: 0 - year, 1 - city, 2 - county, 3 - richter scale
        int colIndex = 0, resultIndex = 0;
        Object[] rowValues = new Object[3];
        //Assign to the first two columns, resulting row, the year and city name
        rowValues[resultIndex++] = row.get(colIndex);
        rowValues[resultIndex++] = row.get(colIndex + 2);
        //Estimate Damage and assign it to the resulting row's last column
        rowValues[resultIndex] = estimator.estimateDamage(row.getString(colIndex+2)
                , Double.valueOf(row.getString(colIndex+3)));
        return new GenericRowWithSchema(rowValues, getSchema());
    }

    //Schema of the return row
    public static StructType getSchema() {
        StructField[] fields = new StructField[]{
                new StructField("Year", DataTypes.StringType, true, null),
                new StructField("Country", DataTypes.StringType, true, null),
                new StructField("Estimated Damage", DataTypes.DoubleType, true, null),
        };
        return new StructType(fields);
    }
}
