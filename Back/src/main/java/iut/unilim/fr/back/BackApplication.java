package iut.unilim.fr.back;

import iut.unilim.fr.back.service.ResourceGetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackApplication implements CommandLineRunner {
    @Autowired
    private ResourceGetterService resourceGetterService;

	public static void main(String[] args) {
		SpringApplication.run(BackApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        resourceGetterService.setValuesFromRessource("R1.01");
    }

}
