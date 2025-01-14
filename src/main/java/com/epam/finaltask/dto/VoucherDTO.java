package com.epam.finaltask.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDTO {
    @AssertTrue(message = "Arrival date cannot be in the past")
    public boolean isArrivalDateValid() {
        return arrivalDate == null || !arrivalDate.isBefore(LocalDate.now());
    }

    @AssertTrue(message = "Eviction date cannot be in the past")
    public boolean isEvictionDateValid() {
        return evictionDate == null || !evictionDate.isBefore(LocalDate.now());
    }

    private String id;

    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotEmpty(message = "Tour type cannot be empty")
    private String tourType;

    @NotEmpty(message = "Transfer type cannot be empty")
    private String transferType;

    @NotEmpty(message = "Hotel type cannot be empty")
    private String hotelType;

    @NotEmpty(message = "Status cannot be empty")
    private String status;

    @NotNull(message = "Arrival date cannot be null")
    private LocalDate arrivalDate;

    @NotNull(message = "Eviction date cannot be null")
    private LocalDate evictionDate;

    private UUID userId;

    private String isHot;
}
