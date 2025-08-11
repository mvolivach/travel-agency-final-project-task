package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.models.*;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapper {

    public Voucher toVoucher(VoucherDTO voucherDTO, User user) {
        if (voucherDTO == null) {
            throw new IllegalArgumentException("VoucherDTO cannot be null");
        }

        return Voucher.builder()
                .title(voucherDTO.getTitle())
                .description(voucherDTO.getDescription())
                .price(voucherDTO.getPrice())
                .tourType(parseEnum(voucherDTO.getTourType(), TourType.class))
                .transferType(parseEnum(voucherDTO.getTransferType(), TransferType.class))
                .hotelType(parseEnum(voucherDTO.getHotelType(), HotelType.class))
                .status(parseEnum(voucherDTO.getStatus(), VoucherStatus.class))
                .arrivalDate(voucherDTO.getArrivalDate())
                .evictionDate(voucherDTO.getEvictionDate())
                .user(user)
                .isHot("true".equalsIgnoreCase(voucherDTO.getIsHot()))
                .build();
    }

    public VoucherDTO toVoucherDTO(Voucher voucher) {
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher cannot be null");
        }

        VoucherDTO dto = new VoucherDTO();
        dto.setId(voucher.getId() != null ? voucher.getId().toString() : null);
        dto.setTitle(voucher.getTitle());
        dto.setDescription(voucher.getDescription());
        dto.setPrice(voucher.getPrice());
        dto.setTourType(voucher.getTourType() != null ? voucher.getTourType().name() : null);
        dto.setTransferType(voucher.getTransferType() != null ? voucher.getTransferType().name() : null);
        dto.setHotelType(voucher.getHotelType() != null ? voucher.getHotelType().name() : null);
        dto.setStatus(voucher.getStatus() != null ? voucher.getStatus().name() : null);
        dto.setArrivalDate(voucher.getArrivalDate());
        dto.setEvictionDate(voucher.getEvictionDate());
        dto.setUserId(voucher.getUser() != null ? voucher.getUser().getId() : null);
        dto.setIsHot(voucher.isHot() ? "true" : "false");
        return dto;
    }

    private <T extends Enum<T>> T parseEnum(String value, Class<T> enumClass) {
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value for enum " + enumClass.getSimpleName() + ": " + value, e);
        }
    }
}
