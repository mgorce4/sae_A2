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
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static iut.unilim.fr.back.controllerBack.DataEncryptionController.encodeData;
import static iut.unilim.fr.back.controllerBack.LogController.writeInCsvLogs;

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

        Optional<Institution> institution_o = institutionRepository.findById(institutionId);
        if (institution_o.isPresent()) {
            Institution institution = institution_o.get();

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
                    writeInCsvLogs("{User} attempt to import teachers from a CSV file, but the file is invalid.");
                }

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(pathDelimitator);
                    String name = getValue(data, headerMap, "nom");
                    String firstName = getValue(data, headerMap, "prenom");
                    String username = getValue(data, headerMap, "username");
                    Optional<UserSyncadia> userAlreadyImported = teacherRepository.findByUsername(username);


                    if (data.length != 0 && userAlreadyImported.isEmpty()) {
                        UserSyncadia teacher = new UserSyncadia();


                        teacher.setLastname(name);
                        teacher.setFirstname(firstName);
                        teacher.setUsername(username);
                        teacher.setPassword(encodeData(username, "unilim"));


                        teacher.setInstitution(institution);

                        teachersToSave.add(teacher);
                        writeInCsvLogs("Teacher imported : \n" +
                                "   - Name : " + name + "\n" +
                                "   - First name : " + firstName + "\n" +
                                "   - Username : " + username);
                        writeInCsvLogs("{User} import a teacher from a csv file.");
                    } else {
                        if (data.length == 0) {
                            writeInCsvLogs("{User} attempt to import teachers from a csv file, but the csv file is empty");
                        } else {
                            writeInCsvLogs("{User} attempt to import teachers from a csv file, but the user is already in the data base.");
                        }
                    }
                }
                if (!teachersToSave.isEmpty()) {
                    List<UserSyncadia> savedUsers = teacherRepository.saveAll(teachersToSave);
                    writeInCsvLogs("{User} saved " + savedUsers.size() + " teacher from a csv file.");

                    List<AccessRight> accessRightsToSave = new ArrayList<>();

                    for (UserSyncadia savedUser : savedUsers) {
                        AccessRight right = new AccessRight();

                        right.setAccessRight(1);

                        right.setIdUser(savedUser.getIdUser());
                        right.setUser(savedUser);

                        accessRightsToSave.add(right);
                        writeInCsvLogs("Access right saved for user : " + savedUser.getUsername() + ". It is a teacher.");
                    }

                    accessRightRepository.saveAll(accessRightsToSave);
                    writeInCsvLogs("{User} add access right ( teacher ) for " +  accessRightsToSave.size() + " users.");
                }
            } catch (NoSuchAlgorithmException e) {
                writeInCsvLogs("{User} attempt to import teachers from a csv file, but an error occurred:"+ e.getMessage());
            }

            if (!teachersToSave.isEmpty()) {
                teacherRepository.saveAll(teachersToSave);
            }
        } else {
            writeInCsvLogs("{User} attempt to import professors from a csv file, but their institution is invalid.");
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