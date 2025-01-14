package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;

import java.util.List;
import java.util.UUID;

public interface VoucherService {
    VoucherDTO create(VoucherDTO voucherDTO);
    VoucherDTO orderVoucher(String id, UUID userId);
    VoucherDTO update(String id, VoucherDTO voucherDTO);
    void delete(String voucherId);
    List<VoucherDTO> findAllByUserId(String userId);
    VoucherDTO changeTourStatus(String id, String status);
    VoucherDTO changeHotStatus(String id, boolean hotStatus);

    VoucherDTO findById(String voucherId);
    List<VoucherDTO> findAllByTourType(String tourType);
    List<VoucherDTO> findAllByTransferType(String transferType);
    List<VoucherDTO> findAllByPrice(String price);
    List<VoucherDTO> findAllByHotelType(String hotelType);
    List<VoucherDTO> findAll();
}
