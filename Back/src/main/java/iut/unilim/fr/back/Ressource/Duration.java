package iut.unilim.fr.back.Ressource;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Duration {
    private int TD;
    private int TP;
    private int CM;
    private int alternance;

    public int getTotal(){
        return TD + TP + CM + alternance;
    }
}
