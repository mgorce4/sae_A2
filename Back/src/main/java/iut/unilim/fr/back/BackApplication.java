package iut.unilim.fr.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackApplication.class, args);
	}
    // Test a localhost:8080/api/pdf/generate?resourceName=R1.01&userName=apoursat, ou autre nom de resource.

}
