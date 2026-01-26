package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.Institution;
import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.repository.InstitutionRepository;
import iut.unilim.fr.back.repository.UserSyncadiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherImportCsvService {

    @Autowired
    private UserSyncadiaRepository teacherRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

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

                if (data.length == 0) {
                    UserSyncadia teacher = new UserSyncadia();

                    teacher.setLastname(getValue(data, headerMap, "nom"));
                    teacher.setFirstname(getValue(data, headerMap, "prenom"));
                    teacher.setUsername(getValue(data, headerMap, "username"));

                    teacher.setInstitution(institution);

                    teachersToSave.add(teacher);
                }

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