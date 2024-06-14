package com.example.controllers;


import com.example.entities.Flight;
import com.example.services.FlightService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@AllArgsConstructor
@Controller
public class FlightController {

    private final FlightService flightService;

    // Flight list
    @GetMapping("flights") // URL
    public String findAll(Model model,
                          @RequestParam(defaultValue = "") String airportFrom,
                          @RequestParam(defaultValue = "") String airportTo) {

//        List<Flight> flights;
//        if (airportFrom.length() > 0 && airportTo.length() > 0)
//            flights = flightService.findAllByAirportFromAndAirportTo(airportFrom, airportTo);
//
//        else if (airportFrom.length() > 0)
//            flights = flightService.findAllByAirportFrom(airportFrom);
//
//        else if (airportTo.length() > 0)
//            flights = flightService.findAllByAirportTo(airportTo);
//
//        else
//            flights = flightService.findAll();
//
//        model.addAttribute("flights", flights);

        Example<Flight> filter = Example.of(new Flight(airportFrom, airportTo));
        model.addAttribute("flights", flightService.findAll(filter));

        return "flight/flight-list"; // vista
    }

    // Detail of a specific flight by id
    @GetMapping("flights/{id}")
    public String findById(Model model, @PathVariable Long id) {
        Optional<Flight> flightOptional = flightService.findById(id);
        if (flightOptional.isPresent())
            model.addAttribute("flight", flightOptional.get());
        else
            model.addAttribute("error", "Not Found");

        return "flight/flight-detail"; // vista
    }

    // Show form to create ONE (1) new flight
    @GetMapping("flights/create")
    public String showCreateForm(Model model) {
        model.addAttribute("flight", new Flight());
        return "flight/flight-form";
    }

    // Save form to create/edit a flight
    @PostMapping("flights")
    public String saveForm(@ModelAttribute Flight flight) {
        flightService.save(flight);
        return "redirect:/flights"; // redirect to findAll handler
    }

    // Show form to edit
    @GetMapping("flights/{id}/edit")
    public String showEditForm(Model model, @PathVariable Long id) {
        Optional<Flight> flightOptional = flightService.findById(id);
        if (flightOptional.isPresent())
            model.addAttribute("flight", flightOptional.get());
        else
            model.addAttribute("error", "Not Found");

        return "flight/flight-form"; // view
    }

    @GetMapping("flights/{id}/delete")
    public String deleteById(@PathVariable Long id) {
        flightService.deleteById(id);
        return "redirect:/flights";
    }
}
