package com.epam.finaltask.repository;

import com.epam.finaltask.models.HotelType;
import com.epam.finaltask.models.TourType;
import com.epam.finaltask.models.TransferType;
import com.epam.finaltask.models.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {
    List<Voucher> findAllByUserId(UUID userId);
    List<Voucher> findAllByTourType(TourType tourType);
    List<Voucher> findAllByTransferType(TransferType transferType);
    List<Voucher> findAllByPrice(Double price);
    List<Voucher> findAllByHotelType(HotelType hotelType);
    List<Voucher> findAllByUserIdIsNull();
}
