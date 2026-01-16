package iut.unilim.fr.back;

import iut.unilim.fr.back.controller.HoursPerStudentController;
import iut.unilim.fr.back.entity.HoursPerStudent;
import iut.unilim.fr.back.service.HoursPerStudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoursPerStudentControllerTest {

    @Mock
    private HoursPerStudentService hoursPerStudentService;

    @InjectMocks
    private HoursPerStudentController controller;

    @Test
    void testGetAllHoursPerStudent() {
        HoursPerStudent h1 = new HoursPerStudent();
        HoursPerStudent h2 = new HoursPerStudent();
        List<HoursPerStudent> list = Arrays.asList(h1, h2);

        when(hoursPerStudentService.getAllHoursPerStudent()).thenReturn(list);

        List<HoursPerStudent> result = controller.getAllHoursPerStudent();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(hoursPerStudentService).getAllHoursPerStudent();
    }

    @Test
    void testGetHoursPerStudentById_Found() {
        Long id = 1L;
        HoursPerStudent h = new HoursPerStudent();
        when(hoursPerStudentService.getHoursPerStudentById(id)).thenReturn(Optional.of(h));

        ResponseEntity<HoursPerStudent> response = controller.getHoursPerStudentById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(h, response.getBody());
    }

    @Test
    void testGetHoursPerStudentById_NotFound() {
        Long id = 99L;
        when(hoursPerStudentService.getHoursPerStudentById(id)).thenReturn(Optional.empty());

        ResponseEntity<HoursPerStudent> response = controller.getHoursPerStudentById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetHoursPerStudentByResourceId() {
        Long resourceId = 50L;
        HoursPerStudent h = new HoursPerStudent();
        List<HoursPerStudent> list = Collections.singletonList(h);

        when(hoursPerStudentService.getHoursPerStudentByResourceId(resourceId)).thenReturn(list);

        List<HoursPerStudent> result = controller.getHoursPerStudentByResourceId(resourceId);

        assertEquals(1, result.size());
        assertEquals(h, result.getFirst());
        verify(hoursPerStudentService).getHoursPerStudentByResourceId(resourceId);
    }

    @Test
    void testCreateHoursPerStudent() {
        HoursPerStudent input = new HoursPerStudent();
        HoursPerStudent created = new HoursPerStudent();

        when(hoursPerStudentService.createHoursPerStudent(input)).thenReturn(created);

        HoursPerStudent result = controller.createHoursPerStudent(input);

        assertEquals(created, result);
        verify(hoursPerStudentService).createHoursPerStudent(input);
    }

    @Test
    void testUpdateHoursPerStudent() {
        Long id = 1L;
        HoursPerStudent input = new HoursPerStudent();
        HoursPerStudent updated = new HoursPerStudent();

        when(hoursPerStudentService.updateHoursPerStudent(eq(id), any(HoursPerStudent.class))).thenReturn(updated);

        HoursPerStudent result = controller.updateHoursPerStudent(id, input);

        assertEquals(updated, result);
        verify(hoursPerStudentService).updateHoursPerStudent(id, input);
    }

    @Test
    void testDeleteHoursPerStudent() {
        Long id = 1L;
        doNothing().when(hoursPerStudentService).deleteHoursPerStudent(id);

        ResponseEntity<Void> response = controller.deleteHoursPerStudent(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(hoursPerStudentService).deleteHoursPerStudent(id);
    }
}
