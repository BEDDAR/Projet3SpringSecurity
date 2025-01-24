package fr.openclassrooms.rental.models;

import fr.openclassrooms.rental.dto.RentalDTO;

import java.util.List;

public class RentalsResponse {

    private List<RentalDTO> rentals;

    public List<RentalDTO> getRentals() {
        return rentals;
    }

    public void setRentals(List<RentalDTO> rentals) {
        this.rentals = rentals;
    }
}
