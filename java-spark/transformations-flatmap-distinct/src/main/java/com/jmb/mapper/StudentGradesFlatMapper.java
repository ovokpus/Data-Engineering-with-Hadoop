package com.jmb.mapper;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import scala.collection.mutable.WrappedArray;
import java.util.*;

import static com.jmb.BasicTransformationsPartTwo.getStudentsFlattenedSchema;

public class StudentGradesFlatMapper implements FlatMapFunction<Row, Row> {

    @Override
    public Iterator<Row> call(Row row) throws Exception {
        Row newRow = new GenericRowWithSchema(mapAndFlattenArrayValues(row),
                getStudentsFlattenedSchema());
        return Arrays.asList(newRow).iterator();
    }

    private Object[] mapAndFlattenArrayValues(Row row) {
        WrappedArray arrayColumn = row.getAs("Grades");

        int finalRowSize = arrayColumn.length() + 1;
        Object[] finalRow = new Object[finalRowSize];
        int rowColsIndex = 0;

        //Add first value of row corresponding to name
        finalRow[rowColsIndex++] = row.getAs("Student_Name");

        //Loop through the column 2 values within the array and add them as separate columns
        String[] arrayColumnValues = (String[]) arrayColumn.array();
        for (int i = 0; i < arrayColumnValues.length; i++) {
            //check if grades has to be retaken
            String currentGrade = arrayColumnValues[i];
            if (Integer.valueOf(arrayColumnValues[i]) < 6) {
                currentGrade = "R";
            }

            finalRow[rowColsIndex++] = currentGrade;
        }
        return finalRow;
    }
}
