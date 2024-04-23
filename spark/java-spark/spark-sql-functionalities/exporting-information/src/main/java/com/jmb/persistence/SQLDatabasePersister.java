package com.jmb.persistence;

import com.jmb.datasource.DerbyDBManager;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.Row;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

public class SQLDatabasePersister implements MapPartitionsFunction<Row, Row> {

    private static final String STUDENTS_NAT_INSERT_QUERY =
            "INSERT INTO STUDENTS_ORIGIN(FIRST_NAME, LAST_NAME, NATIONALITY) VALUES (?, ?, ?)";

    private final DerbyDBManager manager;

    public SQLDatabasePersister(final DerbyDBManager dbManager) {
        this.manager = dbManager;
    }

    @Override
    public Iterator<Row> call(Iterator<Row> rowIterator) throws Exception {
        Connection dbConn = null;
        PreparedStatement preparedStatement = null;

        try {
            dbConn = manager.getConnection();
            preparedStatement = dbConn.prepareStatement(STUDENTS_NAT_INSERT_QUERY);

            while(rowIterator.hasNext()) {
                int rowIndex = 0, parmIndex = 1;
                Row row = rowIterator.next();

                //Set values from Row's field into SQL INSERT statement
                preparedStatement.setString(parmIndex++, row.getString(rowIndex++));
                preparedStatement.setString(parmIndex++, row.getString(rowIndex++));
                preparedStatement.setString(parmIndex, row.getString(rowIndex));
                //Add to batch insert operation
                preparedStatement.addBatch();
            }
            //Flush changes to DB, execute statement
            preparedStatement.executeBatch();

        } catch (SQLException sqle) {
            //Do exception logging/handling and rethrow
            throw sqle;
        } finally {
            dbConn.close();
            preparedStatement.close();
        }
        return rowIterator;
    }
}
