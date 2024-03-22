package com.jmb.mappers;

import com.jmb.model.Car;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;

public class CarMapper implements MapFunction<Row, Car> {

    @Override
    public Car call(Row carRow) throws Exception {
        Car aCar = new Car();
        //Take information from a Row and populate the car domain object
        aCar.setId(carRow.getAs("Identification.ID"));
        aCar.setMake(carRow.getAs("Identification.Make"));
        aCar.setYear(carRow.getAs("Identification.Year"));
        aCar.setFuelType(carRow.getAs("Fuel_Information.Fuel_Type"));
        aCar.setTransmission(carRow.getAs("Engine_Information.Transmission"));
        return aCar;
    }
}
