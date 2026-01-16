package iut.unilim.fr.back.service;

import iut.unilim.fr.back.dto.ResourceDTO;
import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private PathRepository pathRepository;

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private HoursPerStudentRepository hoursPerStudentRepository;

    @Autowired
    private UeCoefficientRepository ueCoefficientRepository;

    @Autowired
    private UERepository ueRepository;

    @Autowired
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;

    @Autowired
    private TeachersForResourceRepository teachersForResourceRepository;

    @Autowired
    private UserSyncadiaRepository userSyncadiaRepository;

    @Autowired
    private RessourceSheetRepository ressourceSheetRepository;

    @Transactional
    public Ressource createResource(ResourceDTO dto) {
        // 1. Create Resource entity
        Ressource resource = new Ressource();
        resource.setLabel(dto.getLabel());
        resource.setName(dto.getName());
        resource.setApogeeCode(dto.getApogeeCode());
        resource.setSemester(dto.getSemester());
        resource.setDiffMultiCompetences(false);

        // Find and set Terms
        if (dto.getTermsCode() != null) {
            List<Terms> termsList = termsRepository.findByCode(dto.getTermsCode());
            if (!termsList.isEmpty()) {
                resource.setTerms(termsList.get(0));
            }
        }

        // Find and set Path
        if (dto.getPathId() != null) {
            Optional<Path> path = pathRepository.findById(dto.getPathId());
            path.ifPresent(resource::setPath);
        }

        // Save Resource
        resource = ressourceRepository.save(resource);

        // 2. Create ResourceSheet
        RessourceSheet resourceSheet = new RessourceSheet();
        resourceSheet.setYear(LocalDate.now());
        resourceSheet.setResource(resource);
        ressourceSheetRepository.save(resourceSheet);

        // 3. Create HoursPerStudent (formation initiale)
        if (dto.getCmInitial() != null || dto.getTdInitial() != null || dto.getTpInitial() != null) {
            HoursPerStudent hoursInitial = new HoursPerStudent();
            Double cm = dto.getCmInitial() != null ? dto.getCmInitial() : 0.0;
            Double td = dto.getTdInitial() != null ? dto.getTdInitial() : 0.0;
            Double tp = dto.getTpInitial() != null ? dto.getTpInitial() : 0.0;
            hoursInitial.setCm(cm);
            hoursInitial.setTd(td);
            hoursInitial.setTp(tp);
            hoursInitial.setHasAlternance(false);
            hoursInitial.setResource(resource);
            hoursPerStudentRepository.save(hoursInitial);
        }

        // 4. Create HoursPerStudent (alternance) if provided
        if (dto.getCmAlternance() != null || dto.getTdAlternance() != null || dto.getTpAlternance() != null) {
            HoursPerStudent hoursAlternance = new HoursPerStudent();
            Double cm = dto.getCmAlternance() != null ? dto.getCmAlternance() : 0.0;
            Double td = dto.getTdAlternance() != null ? dto.getTdAlternance() : 0.0;
            Double tp = dto.getTpAlternance() != null ? dto.getTpAlternance() : 0.0;
            hoursAlternance.setCm(cm);
            hoursAlternance.setTd(td);
            hoursAlternance.setTp(tp);
            hoursAlternance.setHasAlternance(true);
            hoursAlternance.setResource(resource);
            hoursPerStudentRepository.save(hoursAlternance);
        }

        // 5. Create Main Teacher if provided
        if (dto.getMainTeacher() != null && !dto.getMainTeacher().trim().isEmpty()) {
            String[] parts = dto.getMainTeacher().trim().split(" ", 2);
            if (parts.length == 2) {
                List<UserSyncadia> users = userSyncadiaRepository.findByFirstnameAndLastname(parts[0], parts[1]);
                if (!users.isEmpty()) {
                    MainTeacherForResource mainTeacher = new MainTeacherForResource();
                    mainTeacher.setUser(users.get(0));
                    mainTeacher.setResource(resource);
                    mainTeacherForResourceRepository.save(mainTeacher);
                }
            }
        }

        // 6. Create Associated Teachers if provided
        if (dto.getTeachers() != null) {
            for (String teacherName : dto.getTeachers()) {
                if (teacherName != null && !teacherName.trim().isEmpty()) {
                    String[] parts = teacherName.trim().split(" ", 2);
                    if (parts.length == 2) {
                        List<UserSyncadia> users = userSyncadiaRepository.findByFirstnameAndLastname(parts[0], parts[1]);
                        if (!users.isEmpty()) {
                            TeachersForResource teacher = new TeachersForResource();
                            teacher.setUser(users.get(0));
                            teacher.setResource(resource);
                            teachersForResourceRepository.save(teacher);
                        }
                    }
                }
            }
        }

        // 7. Create UE Coefficients
        if (dto.getUeCoefficients() != null) {
            for (ResourceDTO.UeCoefficientDTO coeffDTO : dto.getUeCoefficients()) {
                if (coeffDTO.getUeId() != null) {
                    Optional<UE> ue = ueRepository.findById(coeffDTO.getUeId());
                    if (ue.isPresent()) {
                        UeCoefficient coefficient = new UeCoefficient();
                        Double coeffValue = coeffDTO.getCoefficient();
                        coefficient.setCoefficient(coeffValue);
                        coefficient.setUe(ue.get());
                        coefficient.setResource(resource);
                        ueCoefficientRepository.save(coefficient);
                    }
                }
            }
        }

        return resource;
    }

    @Transactional
    public Ressource updateResource(Long resourceId, ResourceDTO dto) {
        // 1. Find and update Resource
        Ressource resource = ressourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        resource.setLabel(dto.getLabel());
        resource.setName(dto.getName());
        resource.setApogeeCode(dto.getApogeeCode());
        resource.setSemester(dto.getSemester());

        // Update Terms
        if (dto.getTermsCode() != null) {
            List<Terms> termsList = termsRepository.findByCode(dto.getTermsCode());
            if (!termsList.isEmpty()) {
                resource.setTerms(termsList.get(0));
            }
        }

        // Update Path
        if (dto.getPathId() != null) {
            Optional<Path> path = pathRepository.findById(dto.getPathId());
            path.ifPresent(resource::setPath);
        }

        resource = ressourceRepository.save(resource);

        // 2. Update HoursPerStudent
        List<HoursPerStudent> existingHours = hoursPerStudentRepository.findByResource_IdResource(resourceId);

        // Update formation initiale
        HoursPerStudent hoursInitial = existingHours.stream()
                .filter(h -> !h.getHasAlternance())
                .findFirst()
                .orElse(null);

        if (hoursInitial != null) {
            Double cm = dto.getCmInitial() != null ? dto.getCmInitial() : 0.0;
            Double td = dto.getTdInitial() != null ? dto.getTdInitial() : 0.0;
            Double tp = dto.getTpInitial() != null ? dto.getTpInitial() : 0.0;
            hoursInitial.setCm(cm);
            hoursInitial.setTd(td);
            hoursInitial.setTp(tp);
            hoursPerStudentRepository.save(hoursInitial);
        } else if (dto.getCmInitial() != null || dto.getTdInitial() != null || dto.getTpInitial() != null) {
            hoursInitial = new HoursPerStudent();
            Double cm = dto.getCmInitial() != null ? dto.getCmInitial() : 0.0;
            Double td = dto.getTdInitial() != null ? dto.getTdInitial() : 0.0;
            Double tp = dto.getTpInitial() != null ? dto.getTpInitial() : 0.0;
            hoursInitial.setCm(cm);
            hoursInitial.setTd(td);
            hoursInitial.setTp(tp);
            hoursInitial.setHasAlternance(false);
            hoursInitial.setResource(resource);
            hoursPerStudentRepository.save(hoursInitial);
        }

        // Update alternance
        HoursPerStudent hoursAlternance = existingHours.stream()
                .filter(HoursPerStudent::getHasAlternance)
                .findFirst()
                .orElse(null);

        if (dto.getCmAlternance() != null || dto.getTdAlternance() != null || dto.getTpAlternance() != null) {
            if (hoursAlternance != null) {
                Double cm = dto.getCmAlternance() != null ? dto.getCmAlternance() : 0.0;
                Double td = dto.getTdAlternance() != null ? dto.getTdAlternance() : 0.0;
                Double tp = dto.getTpAlternance() != null ? dto.getTpAlternance() : 0.0;
                hoursAlternance.setCm(cm);
                hoursAlternance.setTd(td);
                hoursAlternance.setTp(tp);
                hoursPerStudentRepository.save(hoursAlternance);
            } else {
                hoursAlternance = new HoursPerStudent();
                Double cm = dto.getCmAlternance() != null ? dto.getCmAlternance() : 0.0;
                Double td = dto.getTdAlternance() != null ? dto.getTdAlternance() : 0.0;
                Double tp = dto.getTpAlternance() != null ? dto.getTpAlternance() : 0.0;
                hoursAlternance.setCm(cm);
                hoursAlternance.setTd(td);
                hoursAlternance.setTp(tp);
                hoursAlternance.setHasAlternance(true);
                hoursAlternance.setResource(resource);
                hoursPerStudentRepository.save(hoursAlternance);
            }
        } else if (hoursAlternance != null) {
            hoursPerStudentRepository.delete(hoursAlternance);
        }

        // 3. Update Main Teacher
        List<MainTeacherForResource> existingMainTeachers = mainTeacherForResourceRepository.findByIdResource(resourceId);
        existingMainTeachers.forEach(mainTeacherForResourceRepository::delete);

        if (dto.getMainTeacher() != null && !dto.getMainTeacher().trim().isEmpty()) {
            String[] parts = dto.getMainTeacher().trim().split(" ", 2);
            if (parts.length == 2) {
                List<UserSyncadia> users = userSyncadiaRepository.findByFirstnameAndLastname(parts[0], parts[1]);
                if (!users.isEmpty()) {
                    MainTeacherForResource mainTeacher = new MainTeacherForResource();
                    mainTeacher.setUser(users.get(0));
                    mainTeacher.setResource(resource);
                    mainTeacherForResourceRepository.save(mainTeacher);
                }
            }
        }

        // 4. Update Associated Teachers
        List<TeachersForResource> existingTeachers = teachersForResourceRepository.findByIdResource(resourceId);
        existingTeachers.forEach(teachersForResourceRepository::delete);

        if (dto.getTeachers() != null) {
            for (String teacherName : dto.getTeachers()) {
                if (teacherName != null && !teacherName.trim().isEmpty()) {
                    String[] parts = teacherName.trim().split(" ", 2);
                    if (parts.length == 2) {
                        List<UserSyncadia> users = userSyncadiaRepository.findByFirstnameAndLastname(parts[0], parts[1]);
                        if (!users.isEmpty()) {
                            TeachersForResource teacher = new TeachersForResource();
                            teacher.setUser(users.get(0));
                            teacher.setResource(resource);
                            teachersForResourceRepository.save(teacher);
                        }
                    }
                }
            }
        }

        // 5. Update UE Coefficients
        List<UeCoefficient> existingCoefficients = ueCoefficientRepository.findByResource_IdResource(resourceId);
        existingCoefficients.forEach(ueCoefficientRepository::delete);

        if (dto.getUeCoefficients() != null) {
            for (ResourceDTO.UeCoefficientDTO coeffDTO : dto.getUeCoefficients()) {
                if (coeffDTO.getUeId() != null) {
                    Optional<UE> ue = ueRepository.findById(coeffDTO.getUeId());
                    if (ue.isPresent()) {
                        UeCoefficient coefficient = new UeCoefficient();
                        Double coeffValue = coeffDTO.getCoefficient();
                        coefficient.setCoefficient(coeffValue);
                        coefficient.setUe(ue.get());
                        coefficient.setResource(resource);
                        ueCoefficientRepository.save(coefficient);
                    }
                }
            }
        }

        return resource;
    }

    @Transactional
    public void deleteResource(Long resourceId) {
        // Delete in order: resource sheets, hours, coefficients, teachers, resource
        List<RessourceSheet> resourceSheets = ressourceSheetRepository.findByResource_IdResource(resourceId);
        resourceSheets.forEach(ressourceSheetRepository::delete);

        List<HoursPerStudent> hours = hoursPerStudentRepository.findByResource_IdResource(resourceId);
        hours.forEach(hoursPerStudentRepository::delete);

        List<UeCoefficient> coefficients = ueCoefficientRepository.findByResource_IdResource(resourceId);
        coefficients.forEach(ueCoefficientRepository::delete);

        List<MainTeacherForResource> mainTeachers = mainTeacherForResourceRepository.findByIdResource(resourceId);
        mainTeachers.forEach(mainTeacherForResourceRepository::delete);

        List<TeachersForResource> teachers = teachersForResourceRepository.findByIdResource(resourceId);
        teachers.forEach(teachersForResourceRepository::delete);

        ressourceRepository.deleteById(resourceId);
    }
}

