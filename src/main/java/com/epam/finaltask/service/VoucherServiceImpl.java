package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.exception.StatusCodes;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.models.*;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final UserRepository userRepository;

    public VoucherServiceImpl(VoucherRepository voucherRepository, VoucherMapper voucherMapper, UserRepository userRepository) {
        this.voucherRepository = voucherRepository;
        this.voucherMapper = voucherMapper;
        this.userRepository = userRepository;
    }

    @Override
    public VoucherDTO changeTourStatus(String id, String status) {
        Voucher existingVoucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Voucher with Id " + id + " not found", StatusCodes.ENTITY_NOT_FOUND));

        if ("CANCELED".equalsIgnoreCase(status) || "REGISTERED".equalsIgnoreCase(status)) {
            User user = existingVoucher.getUser();
            if (user != null) {
                user.setBalance(user.getBalance() + existingVoucher.getPrice());
                existingVoucher.setUser(null);
                userRepository.save(user);
            }
        }

        try {
            VoucherStatus voucherStatus = VoucherStatus.valueOf(status.toUpperCase());
            existingVoucher.setStatus(voucherStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Allowed values are: PAID, CANCELED");
        }

        Voucher savedVoucher = voucherRepository.save(existingVoucher);
        return voucherMapper.toVoucherDTO(savedVoucher);
    }

    @Override
    public void cancelVoucher(String voucherId, UUID userId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found", StatusCodes.ENTITY_NOT_FOUND));

        if (voucher.getUser() == null || !voucher.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You do not own this voucher");
        }

        if (voucher.getStatus() != VoucherStatus.PAID) {
            throw new IllegalStateException("Only paid vouchers can be canceled");
        }

        User user = voucher.getUser();
        user.setBalance(user.getBalance() + voucher.getPrice());
        userRepository.save(user);

        voucher.setUser(null);
        voucher.setStatus(VoucherStatus.REGISTERED);

        voucherRepository.save(voucher);
    }


    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        if (voucherDTO.getArrivalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Arrival date cannot be in the past");
        }

        if (voucherDTO.getEvictionDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Eviction date cannot be in the past");
        }

        if (voucherDTO.getEvictionDate().isBefore(voucherDTO.getArrivalDate())) {
            throw new IllegalArgumentException("Eviction date cannot be earlier than arrival date");
        }

        User user = null;
        if (voucherDTO.getUserId() != null) {
            user = userRepository.findById(voucherDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found", StatusCodes.ENTITY_NOT_FOUND));
        }

        Voucher voucher = voucherMapper.toVoucher(voucherDTO, user);

        Voucher savedVoucher = voucherRepository.save(voucher);

        return voucherMapper.toVoucherDTO(savedVoucher);
    }

    @Override
    public VoucherDTO orderVoucher(String voucherId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", StatusCodes.ENTITY_NOT_FOUND));

        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found", StatusCodes.ENTITY_NOT_FOUND));

        if (voucher.getUser() != null) {
            throw new IllegalStateException("Voucher is already ordered");
        }

        if (voucher.getStatus() == VoucherStatus.PAID || voucher.getStatus() == VoucherStatus.CANCELED) {
            throw new IllegalStateException("Voucher with status 'PAID' or 'CANCELED' cannot be ordered");
        }

        if (user.getBalance() < voucher.getPrice()) {
            throw new IllegalStateException("Insufficient funds to order this voucher");
        }

        user.setBalance(user.getBalance() - voucher.getPrice());
        userRepository.save(user);

        voucher.setUser(user);

        voucher.setStatus(VoucherStatus.PAID);

        Voucher savedVoucher = voucherRepository.save(voucher);

        return voucherMapper.toVoucherDTO(savedVoucher);
    }

    @Override
    public VoucherDTO findById(String voucherId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new EntityNotFoundException("Voucher with Id " + voucherId + " not found", StatusCodes.ENTITY_NOT_FOUND));
        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        Voucher existingVoucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Voucher with Id " + id + " not found", StatusCodes.ENTITY_NOT_FOUND));

        if (existingVoucher.getStatus() == VoucherStatus.PAID) {
            throw new IllegalStateException("Voucher status is PAID and cannot be modified. Change the status first.");
        }

        if (voucherDTO.getArrivalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Arrival date cannot be in the past");
        }

        if (voucherDTO.getEvictionDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Eviction date cannot be in the past");
        }

        if (voucherDTO.getEvictionDate().isBefore(voucherDTO.getArrivalDate())) {
            throw new IllegalArgumentException("Eviction date cannot be earlier than arrival date");
        }

        User user = null;
        if (voucherDTO.getUserId() != null) {
            user = userRepository.findById(voucherDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found", StatusCodes.ENTITY_NOT_FOUND));
        }

        Voucher updatedVoucher = voucherMapper.toVoucher(voucherDTO, user);
        updatedVoucher.setId(existingVoucher.getId());

        Voucher savedVoucher = voucherRepository.save(updatedVoucher);
        return voucherMapper.toVoucherDTO(savedVoucher);
    }

    @Override
    public void delete(String voucherId) {
        UUID id = UUID.fromString(voucherId);

        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Voucher with Id " + voucherId + " not found", StatusCodes.ENTITY_NOT_FOUND));

        if (voucher.getStatus() == VoucherStatus.PAID && voucher.getUser() != null) {
            User user = voucher.getUser();
            user.setBalance(user.getBalance() + voucher.getPrice());
            userRepository.save(user);
        }

        voucherRepository.deleteById(id);
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        List<Voucher> vouchers = voucherRepository.findAllByUserId(UUID.fromString(userId));
        return (vouchers != null && !vouchers.isEmpty()) ?
                vouchers.stream()
                        .sorted((v1, v2) -> Boolean.compare(v2.isHot(), v1.isHot()))
                        .map(voucherMapper::toVoucherDTO)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    @Override
    public List<VoucherDTO> findAllByTourType(String tourType) {
        return voucherRepository.findAllByTourType(TourType.valueOf(tourType)).stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> filterVouchers(String tourType, String transferType, String hotelType, Double minPrice, Double maxPrice) {
        return voucherRepository.findAll().stream()
                .filter(v -> tourType == null || v.getTourType().name().equalsIgnoreCase(tourType))
                .filter(v -> transferType == null || v.getTransferType().name().equalsIgnoreCase(transferType))
                .filter(v -> hotelType == null || v.getHotelType().name().equalsIgnoreCase(hotelType))
                .filter(v -> minPrice == null || v.getPrice() >= minPrice)
                .filter(v -> maxPrice == null || v.getPrice() <= maxPrice)
                .sorted((v1, v2) -> Boolean.compare(v2.isHot(), v1.isHot()))
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        return voucherRepository.findAllByTransferType(TransferType.valueOf(transferType)).stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByPrice(String price) {
        double targetPrice = Double.parseDouble(price);
        return voucherRepository.findAllByPrice(targetPrice).stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(String hotelType) {
        return voucherRepository.findAllByHotelType(HotelType.valueOf(hotelType)).stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VoucherDTO changeHotStatus(String id, boolean hotStatus) {
        Voucher existingVoucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Voucher with Id " + id + " not found", StatusCodes.ENTITY_NOT_FOUND));

        existingVoucher.setHot(hotStatus);

        Voucher savedVoucher = voucherRepository.save(existingVoucher);

        return voucherMapper.toVoucherDTO(savedVoucher);
    }

    @Override
    public List<VoucherDTO> findAll() {
        return voucherRepository.findAll().stream()
                .sorted((v1, v2) -> Boolean.compare(v2.isHot(), v1.isHot()))
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }
}

