package pe.edu.upeu.dad.instructor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.dad.instructor.service.InstructorService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Prueba de integracion de la capa web (controller + manejo de errores)
@WebMvcTest(InstructorController.class)
class InstructorControllerWebTest {

    @Autowired MockMvc mvc;
    @MockBean InstructorService service;

    @Test
    void listar_devuelve200() throws Exception {
        when(service.listar()).thenReturn(List.of());
        mvc.perform(get("/api/instructores")).andExpect(status().isOk());
    }

    @Test
    void crear_conDatosInvalidos_devuelve400() throws Exception {
        String bodyInvalido = "{\"dni\":\"123\",\"nombres\":\"\",\"apellidos\":\"X\",\"especialidad\":\"X\",\"email\":\"malo\"}";
        mvc.perform(post("/api/instructores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest());
    }
}
