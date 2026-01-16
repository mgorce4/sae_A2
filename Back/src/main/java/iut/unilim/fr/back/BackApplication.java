package iut.unilim.fr.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackApplication.class, args);
	}
    // Test pdf at localhost:8080/api/pdf/generate?resourceName=R1.01&userName=apoursat
    // Test csv at http://localhost:8080/api/csv/generate?resourceName=R1.01&userDepartment=INFORMATIQUE&userName=apoursat

}
