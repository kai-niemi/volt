package io.roach.volt.csv.model;

import jakarta.validation.constraints.NotEmpty;

import java.util.Collections;
import java.util.List;

public class ValueSet<T> {
    @NotEmpty
    private List<T> values;

    private List<Double> weights = Collections.emptyList();

    public List<T> getValues() {
        return values;
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public void setWeights(List<Double> weights) {
        this.weights = weights;
    }
}
