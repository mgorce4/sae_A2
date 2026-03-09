package iut.unilim.fr.back.resource;


public class Duration {
    private int TD;
    private int TP;
    private int CM;
    private int alternance;

    public int getTotal(){
        return TD + TP + CM + alternance;
    }
}
