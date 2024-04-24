package com.jmb.model;

import com.jmb.Estimator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class represents a crude and simple damage percentage estimation based on input data for
 * earthquakes.
 */
public final class DamageEstimatorSerializable implements Estimator, Serializable {

    private static Map<String, Integer> infraIndicesMap;

    static {
        infraIndicesMap = new HashMap<>();
        infraIndicesMap.put("Argentina", 70);
        infraIndicesMap.put("Chile", 85);
        infraIndicesMap.put("New Zealand", 50);
        infraIndicesMap.put("Japan", 95);
    }
    public double estimateDamage(String country, double richterScale) {
        if(Objects.isNull(infraIndicesMap.get(country))) {
            throw new IllegalArgumentException("Country not present in DB");
        }

        return (infraIndicesMap.get(country) * richterScale) / 100;
    }
}
