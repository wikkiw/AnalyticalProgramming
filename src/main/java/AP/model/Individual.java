package AP.model;

import java.util.Arrays;

/**
 * Created by jakub on 27/10/15.
 */
public class Individual {

    public double[] vector;
    public double fitness;
    public String id;

    public Individual() {
    }

    public Individual(String id, double[] vector, double fitness) {
        this.id = id;
        this.vector = vector;
        this.fitness = fitness;
    }

    public Individual(Individual individual) {
        this.id = individual.id;
        this.vector = individual.vector.clone();
        this.fitness = individual.fitness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Individual that = (Individual) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Individual{" +
                "id=" + id +
                ", vector=" + Arrays.toString(vector) +
                ", fitness=" + fitness +
                '}';
    }
}
