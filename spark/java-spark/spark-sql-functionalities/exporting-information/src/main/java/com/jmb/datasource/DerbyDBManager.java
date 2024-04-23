package com.jmb.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public final class DerbyDBManager implements Serializable {

    public static final String TABLE_NAME = "Student";
    public static final String DB_URL = "jdbc:derby:firstdb;create=true;user=app;password=derby";

    private static final String PATH_RESOURCES = "src/main/resources/spark-data/db_inserts.txt";
    private static final Logger LOGGER = LoggerFactory.getLogger(DerbyDBManager.class);

    public void startDB() throws SQLException {
        LOGGER.info("Starting Derby Embedded DB ... ");
        Connection conn = DriverManager.getConnection(DB_URL);
        createTable(conn);
        doInserts(conn);
        createNationalitiesTable(conn);
        close(conn, null, null);
    }

    public void stopDB() throws SQLException {
        LOGGER.info("Stopping Derby Embedded DB ... ");
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            //Drop table if it already exists
            LOGGER.info("Deleting STUDENT table in DB ... ");
            stmt.executeUpdate("Drop Table " + TABLE_NAME);
        } catch (SQLException e) {
            //Log exception and rethrow
            LOGGER.error("Error while creating the TABLE in DB: " + e.getMessage());
            throw e;
        }
        close(conn, null, null);
    }

    /**
     * Naive warning: This is an extremely simplistic way of providing connections, used for
     * illustrative purposes; a DB Manager should ALWAYS aim at using a Datasource Pool for connection
     * pooling such as Hiraki or C3P0.
     *
     * @return A connection to the Derby DB
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void printStudentNationalitiesTable() throws SQLException {
        LOGGER.info("Printing rows in STUDENTS_ORIGIN table ... ");
        String query = "SELECT * FROM STUDENTS_ORIGIN";
        Connection conn = this.getConnection();
        try(PreparedStatement statement = conn.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                int index = 1;
                LOGGER.info("ID: " + rs.getString(index++) + " FIRST_NAME: " + rs.getString(index++)
                 + " LAST_NAME: " + rs.getString(index++) + " NATIONALITY: " + rs.getString(index));
            }
        } finally {
            if(!conn.isClosed())
                conn.close();
        }
    }

    private void createTable(Connection connection) throws SQLException {
        LOGGER.info("Creating STUDENT table in DB ... ");
        try (Statement stmt = connection.createStatement()) {
            //Drop table if it already exists
            //stmt.executeUpdate("Drop Table " + TABLE_NAME);
            // create placeholder empty table
            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    "   Id INT NOT NULL GENERATED ALWAYS AS IDENTITY," +
                    "   Age INT NOT NULL," +
                    "   First_Name VARCHAR(255)," +
                    "   last_name VARCHAR(255)," +
                    "   PRIMARY KEY (Id))";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            //Log exception and rethrow
            LOGGER.error("Error while creating the TABLE in DB: " + e.getMessage());
            throw e;
        }
    }

    private void createNationalitiesTable(Connection connection) throws SQLException {
        LOGGER.info("Creating STUDENTS_ORIGIN table in the DB");
        String query = "CREATE TABLE STUDENTS_ORIGIN (" +
                "   Id INT NOT NULL GENERATED ALWAYS AS IDENTITY," +
                "   First_Name VARCHAR(255)," +
                "   Last_name VARCHAR(255)," +
                "   Nationality VARCHAR(255)," +
                "   PRIMARY KEY (Id))";
        try(Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
        //Log exception and rethrow
        LOGGER.error("Error while creating the TABLE in DB: " + e.getMessage());
        throw e;
        }
    }

    private void doInserts(Connection conn) {
        LOGGER.info("Inserting Rows in STUDENT table ... ");
        try {
            List<String> allLines = Files.readAllLines(Paths.get(PATH_RESOURCES));
            for (String line : allLines) {
                try(PreparedStatement stmt = conn.prepareStatement(line)) {
                    stmt.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(Connection conn, PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
        }
    }

}
