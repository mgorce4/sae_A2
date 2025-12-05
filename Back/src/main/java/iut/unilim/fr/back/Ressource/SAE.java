package iut.unilim.fr.back.Ressource;

public class SAE {
    private String name;
    private String APOGEECode;
    private int hours;

    public SAE(String name, String APOGEECode, int hours) {
        this.name = name;
        this.APOGEECode = APOGEECode;
        this.hours = hours;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAPOGEECode() {
        return APOGEECode;
    }
}
