package com.mahghuuuls.elusiveinfusions.roster;

/** A number of one Elusive Flora plant, named by the path of its registry id. */
public final class PlantCount {

    private final String plant;
    private final int count;

    PlantCount(String plant, int count) {
        this.plant = plant;
        this.count = count;
    }

    public String plant() {
        return plant;
    }

    public int count() {
        return count;
    }
}
