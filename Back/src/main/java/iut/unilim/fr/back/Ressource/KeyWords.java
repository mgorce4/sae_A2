package iut.unilim.fr.back.Ressource;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class KeyWords {
    private List<String> list;

    public List<String> getter() {
        return list;
    }


    public void setter(List<String> list) {
        this.list = list;
    }
}
