package com.example.controllers;

import com.example.entities.Passenger;
import com.example.entities.Ticket;
import com.example.services.FileService;
import com.example.services.FlightService;
import com.example.services.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@AllArgsConstructor
@Controller
public class TicketController {
    
    private final TicketService ticketService;
    private final FlightService flightService;
    private final FileService fileService;

    // retrieve unfiltered tickets
    @GetMapping("tickets") 
    public String findAll(Model model) {
        model.addAttribute("tickets", ticketService.findAll());
        return "ticket/ticket-list"; 
    }

    // retrieves the logged-in user's tickets
    @GetMapping("user-tickets")
    public String findAllByCurrentUser(Model model) {
        Passenger currentUser = (Passenger) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("tickets", ticketService.findAllByPassengerId(currentUser.getId()));
        return "ticket/ticket-list";
    }

    @GetMapping("tickets/{id}")
    public String findById(Model model, @PathVariable Long id) {
        Optional<Ticket> ticketOptional = ticketService.findById(id);
        if (ticketOptional.isPresent())
            model.addAttribute("ticket", ticketOptional.get());
        else
            model.addAttribute("error", "Not Found");

        return "ticket/ticket-detail"; // vista
    }

    @GetMapping("tickets/{id}/buy")
    public String buyTicketForCurrentUser(Model model, @PathVariable Long id) {
        try {
            ticketService.buyTicketForCurrentUser(id);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "The purchase could not be completed");
            model.addAttribute("tickets", ticketService.findAll());
            return "ticket/ticket-list";
        }
        return "redirect:/user-tickets";
    }

    @GetMapping("tickets/create")
    public String showCreateForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("flights", flightService.findAll()); // filter and sort by date
        return "ticket/ticket-form";
    }

    // Save form to create/edit a flight
    @PostMapping("tickets")
    public String saveForm(Model model,
                           @ModelAttribute Ticket ticket,
                           @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            ticketService.save(ticket);
            return "redirect:/tickets";
        }

        try {
            String fileName = fileService.storeInFileSystem(file);
            ticket.setImageUrl(fileName); // string
            ticketService.save(ticket);
            return "redirect:/tickets"; // redirect to findAll controller
        } catch (Exception e) {
            model.addAttribute("error", "Could not save image");
            model.addAttribute("tickets", ticketService.findAll());
            return "ticket/ticket-list";
        }

    }

    // Show form to edit
    @GetMapping("tickets/{id}/edit")
    public String showEditForm(Model model, @PathVariable Long id) {
        Optional<Ticket> ticketOptional = ticketService.findById(id);
        if (ticketOptional.isPresent()) {
            model.addAttribute("ticket", ticketOptional.get());
            model.addAttribute("flights", flightService.findAll());
        } else {
            model.addAttribute("error", "Not Found");
        }

        return "ticket/ticket-form"; // view
    }

    @GetMapping("tickets/{id}/delete")
    public String deleteById(@PathVariable Long id) {
        ticketService.deleteById(id);
        return "redirect:/tickets";
    }

}
