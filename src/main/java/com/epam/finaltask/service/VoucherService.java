package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface VoucherService {
    VoucherDTO create(VoucherDTO voucherDTO);
    VoucherDTO orderVoucher(String id, UUID userId);
    VoucherDTO update(String id, VoucherDTO voucherDTO);
    void delete(String voucherId);
    Page<VoucherDTO> findAllByUserId(String userId, Pageable pageable);
    VoucherDTO changeTourStatus(String id, String status);
    VoucherDTO changeHotStatus(String id, boolean hotStatus);
    void cancelVoucher(String voucherId, UUID userId);

    VoucherDTO findById(String voucherId);
    List<VoucherDTO> findAllByTourType(String tourType);
    List<VoucherDTO> findAllByTransferType(String transferType);
    List<VoucherDTO> findAllByPrice(String price);
    List<VoucherDTO> findAllByHotelType(String hotelType);
    Page<VoucherDTO> filterVouchers(String tourType, String transferType,  String hotelType, Double minPrice, Double maxPrice,Pageable pageable);
    Page<VoucherDTO> findAll(Pageable pageable);
}
