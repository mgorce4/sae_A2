package iut.unilim.fr.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class BackApplication implements CommandLineRunner {

    @Autowired
    private MailTestSender mailTestSender;

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
    // Test pdf at localhost:8080/api/pdf/generate?resourceName=R1.01&userName=apoursat
    // Test csv at http://localhost:8080/api/csv/generate?resourceName=R1.01&userDepartment=INFORMATIQUE&userName=apoursat

}
