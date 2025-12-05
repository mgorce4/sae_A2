package iut.unilim.fr.back.Ressource;

public class UE {
    private int UENumber;
    private String APOGEECode;

    public  UE(int UENumber, String APOGEECode) {
        this.UENumber = UENumber;
        this.APOGEECode = APOGEECode;
    }

    public int getUENumber() {
        return UENumber;
    }

    public void setUENumber(int UENumber) {
        this.UENumber = UENumber;
    }

    public  String getAPOGEECode() {
        return APOGEECode;
    }

    public void setAPOGEECode(String APOGEECode) {
        this.APOGEECode = APOGEECode;
    }
}
