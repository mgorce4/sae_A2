package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.AccessRight;
import iut.unilim.fr.back.entity.Institution;
import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.repository.AccessRightRepository;
import iut.unilim.fr.back.repository.InstitutionRepository;
import iut.unilim.fr.back.repository.UserSyncadiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class TeacherImportCsvService {

    @Autowired
    private UserSyncadiaRepository teacherRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private AccessRightRepository accessRightRepository;

    @Transactional
    public void importTeachers(MultipartFile file, Long institutionId) throws IOException {

        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new RuntimeException("Institution introuvable avec l'ID : " + institutionId));

        List<UserSyncadia> teachersToSave = new ArrayList<>();
        String pathDelimitator = ";";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return;

            Map<String, Integer> headerMap = new HashMap<>();
            String[] headers = headerLine.split(pathDelimitator);

            for (int i = 0; i < headers.length; i++) {
                String headerName = removeUTF8BOM(headers[i].trim());
                headerMap.put(headerName.toLowerCase(), i);
            }

            if (!headerMap.containsKey("prenom") || !headerMap.containsKey("nom") || !headerMap.containsKey("username")){
                throw new IOException("Le fichier CSV doit contenir les colonnes 'Nom', 'Prenom' et 'username");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(pathDelimitator);
                String name = getValue(data, headerMap, "nom");
                String firstName = getValue(data, headerMap, "prenom");
                String username = getValue(data, headerMap, "username");

                Optional<UserSyncadia> userAlreadyImported = teacherRepository.findByUsername(username);

                System.out.println();
                System.out.println("toto1");
                System.out.println("name :" + name);
                System.out.println("First name :" + firstName);
                System.out.println("Username" + username);
                System.out.println(userAlreadyImported.isEmpty());
                System.out.println();



                if (data.length != 0 && userAlreadyImported.isEmpty()) {
                    System.out.println("\ntoto2");
                    UserSyncadia teacher = new UserSyncadia();


                    teacher.setLastname(name);
                    teacher.setFirstname(firstName);
                    teacher.setUsername(username);
                    teacher.setPassword(username + "1234");


                    teacher.setInstitution(institution);

                    teachersToSave.add(teacher);
                    System.out.println(teachersToSave.getLast().getUsername());
                    System.out.println("Un user a ete enregistree\n");
                }
            }
            if (!teachersToSave.isEmpty()) {
                List<UserSyncadia> savedUsers = teacherRepository.saveAll(teachersToSave);
                System.out.println(savedUsers.size() + " utilisateurs sauvegardés.");

                List<AccessRight> accessRightsToSave = new ArrayList<>();

                for (UserSyncadia savedUser : savedUsers) {
                    AccessRight right = new AccessRight();

                    right.setAccessRight(1);

                    right.setIdUser(savedUser.getIdUser());
                    right.setUser(savedUser);

                    accessRightsToSave.add(right);
                    System.out.println("Access right" + accessRightsToSave.getLast().getUser());
                }

                accessRightRepository.saveAll(accessRightsToSave);
                System.out.println("Droits d'accès ajoutés pour " + accessRightsToSave.size() + " utilisateurs.");
            }
        }

        if (!teachersToSave.isEmpty()) {
            teacherRepository.saveAll(teachersToSave);
        }
    }

    private String getValue(String[] data, Map<String, Integer> map, String colName) {
        Integer index = map.get(colName.toLowerCase());
        if (index != null && index < data.length) {
            return data[index].trim();
        }
        return "";
    }

    private String removeUTF8BOM(String s) {
        if (s.startsWith("\uFEFF")) {
            return s.substring(1);
        }
        return s;
    }
}