package com.example.services;

import com.example.entities.Passenger;
import com.example.entities.Ticket;
import com.example.exceptions.InsufficientBalanceException;
import com.example.exceptions.TicketAlreadyPurchasedException;
import com.example.repositories.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepo;
    private final PassengerService passengerService;

    @Override
    public List<Ticket> findAll() {
        return ticketRepo.findAll();
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        return ticketRepo.findById(id);
    }

    @Override
    public List<Ticket> findAllByPriceBetween(double min, double max) {
        return ticketRepo.findAllByPriceBetween(min, max);
    }

    @Override
    public List<Ticket> findAllByPriceLessThanEqual(double price) {
        return ticketRepo.findAllByPriceLessThanEqual(price);
    }

    @Override
    public Ticket save(Ticket ticket) {
        return ticketRepo.save(ticket);
    }

    @Override
    public void deleteById(Long id) {
        ticketRepo.deleteById(id);
    }

    @Override
    public List<Ticket> findAllByPassengerId(Long id) {
        return ticketRepo.findAllByPassengerId(id);
    }

    @Override
    public void buyTicketForCurrentUser(Long ticketId) {
        Passenger passenger = (Passenger) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();

        // check if the ticket already has a passenger
        if (ticket.getPassenger() != null)
            throw new TicketAlreadyPurchasedException();

        // check if the passenger does NOT have a sufficient balance
        if (ticket.getPrice() > passenger.getBalance())
            throw new InsufficientBalanceException();

        // associate passenger with ticket and save
        ticket.setPassenger(passenger);
        this.save(ticket);

        // subtract balance from passenger
        Double balanceAfterBuy = passenger.getBalance() - ticket.getPrice();
        passenger.setBalance(balanceAfterBuy);
        passengerService.save(passenger);
    }

    @Override
    public List<Ticket> findAllByFlightId(Long id) {
        return ticketRepo.findAllByFlightId(id);
    }

    @Override
    public List<Ticket> saveAll(List<Ticket> tickets) {
        return ticketRepo.saveAll(tickets);
    }
}
