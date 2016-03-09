package problemdescription;

/**
 * Created by Michal Dorko on 09/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class City {

    private int posx;
    private int posy;

    public City(int posx, int posy) {
        this.posx = posx;
        this.posy = posy;
    }

    public int getPosx() {
        return posx;
    }

    public void setPosx(int posx) {
        this.posx = posx;
    }

    public int getPosy() {
        return posy;
    }

    public void setPosy(int posy) {
        this.posy = posy;
    }
}
