package com.epam.finaltask.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
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
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    private boolean isHot;

    public Voucher() {

    }

    public Voucher(UUID id, String title, String description, double price, TourType tourType, TransferType transferType, HotelType hotelType, VoucherStatus status, LocalDate arrivalDate, LocalDate evictionDate, User user, boolean isHot) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.tourType = tourType;
        this.transferType = transferType;
        this.hotelType = hotelType;
        this.status = status;
        this.arrivalDate = arrivalDate;
        this.evictionDate = evictionDate;
        this.user = user;
        this.isHot = isHot;
    }
}
