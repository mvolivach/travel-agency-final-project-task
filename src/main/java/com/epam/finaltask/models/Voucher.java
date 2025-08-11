package com.epam.finaltask.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    private double price;

    @Enumerated(EnumType.STRING)
    private TourType tourType;

    @Enumerated(EnumType.STRING)
    private TransferType transferType;

    @Enumerated(EnumType.STRING)
    private HotelType hotelType;

    @Enumerated(EnumType.STRING)
    private VoucherStatus status = VoucherStatus.REGISTERED;

    private LocalDate arrivalDate;

    private LocalDate evictionDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isHot;

}
