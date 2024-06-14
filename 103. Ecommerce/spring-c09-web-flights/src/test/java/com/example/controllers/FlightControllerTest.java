package com.example.controllers;

import com.example.entities.Flight;
import com.example.services.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@AutoConfigureMockMvc
//@SpringBootTest
@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    FlightService flightService;

    @Test
    @WithMockUser
    @DisplayName("Recover all flights")
    void findAll() throws Exception {

        // configure mock
        List<Flight> flights = new ArrayList<>(List.of(
                new Flight(),
                new Flight()
        ));
        when(flightService.findAll()).thenReturn(flights);

        // execute method to test
        mvc.perform(get("/flights")).andExpectAll(
                status().isOk(),
                content().contentType("text/html;charset=UTF-8"),
                model().attributeExists("flights"),
//                model().attribute("message1", "Hello World"),
                model().attribute("flights", hasSize(2)),
                view().name("flight/flight-list")
        );
        verify(flightService).findAll();
    }

    @Test
    @WithMockUser
    @DisplayName("Retrieve flight by id")
    void findById() throws Exception {

        Flight flight = new Flight(1L);
        when(flightService.findById(1L)).thenReturn(Optional.of(
                flight
        ));

        mvc.perform(get("/flights/1")).andExpectAll(
            status().isOk(),
            content().contentType("text/html;charset=UTF-8"),
            model().attributeExists("flight"),
            model().attributeDoesNotExist("error"),
            model().attribute("flight", flight),
            view().name("flight/flight-detail")
        );

        verify(flightService).findById(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Recover flight by id that does not exist")
    void findByIdNotFound() throws Exception {

        when(flightService.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(get("/flights/1")).andExpectAll(
                status().isOk(),
                content().contentType("text/html;charset=UTF-8"),
                model().attributeExists("error"),
                model().attributeDoesNotExist("flight"),
                view().name("flight/flight-detail")
        );

        verify(flightService).findById(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Save flight")
    void saveForm() throws Exception {

        mvc.perform(post("/flights").with(csrf())).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl("/flights")
        );

        verify(flightService).save(any());
    }

}