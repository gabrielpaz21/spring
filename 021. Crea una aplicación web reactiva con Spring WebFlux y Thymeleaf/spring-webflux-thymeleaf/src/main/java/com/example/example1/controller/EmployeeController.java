package com.example.example1.controller;

import com.example.example1.model.Employee;
import com.example.example1.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

@Controller
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // Modo FULL
    // Modo CHUNKED: añadir propiedad spring.thymeleaf.reactive.max-chunk-size=1024 en application.properties
    @GetMapping("/employees")
    public String findAll(Model model){
        Flux<Employee> employees = service.findAll();
        model.addAttribute("employees", employees);
        return "employee-list";
    }

    @GetMapping("/employees/reactive")
    public String findAllReactive(Model model){
        Flux<Employee> employees = service.findAll();
        model.addAttribute("employees", new ReactiveDataDriverContextVariable(employees, 50));
        return "employee-list";
    }
}
