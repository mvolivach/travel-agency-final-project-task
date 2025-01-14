package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.exception.StatusCodes;
import com.epam.finaltask.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<Map<String, Object>> findAll() {
        List<VoucherDTO> vouchers = voucherService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("results", vouchers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<Map<String, Object>> findAllByUserId(@PathVariable String userId) {
        List<VoucherDTO> vouchers = voucherService.findAllByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("results", vouchers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/voucher/{voucherId}")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<VoucherDTO> findVoucherById(@PathVariable String voucherId) {
        VoucherDTO voucher = voucherService.findById(voucherId);
        return ResponseEntity.ok(voucher);
    }

    @PostMapping("/order/{voucherId}")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<?> orderVoucher(@PathVariable String voucherId, @RequestBody Map<String, String> request) {
        try {
            UUID userId = UUID.fromString(request.get("userId"));
            VoucherDTO updatedVoucher = voucherService.orderVoucher(voucherId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", "Voucher successfully ordered");
            response.put("data", updatedVoucher);

            return ResponseEntity.ok(response);
        } catch (IllegalStateException | EntityNotFoundException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.BAD_REQUEST.name());
            response.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INTERNAL_ERROR.name());
            response.put("statusMessage", "An error occurred while ordering the voucher");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/allVouchers")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<Map<String, Object>> findAllVouchers() {
        List<VoucherDTO> vouchers = voucherService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("results", vouchers);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    public ResponseEntity<Map<String, String>> createVoucher(@Valid @RequestBody VoucherDTO voucherDTO) {
        voucherService.create(voucherDTO);
        Map<String, String> response = new HashMap<>();
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher is successfully created");
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/change/{voucherId}")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<Map<String, String>> updateVoucher(@PathVariable String voucherId, @Valid @RequestBody VoucherDTO voucherDTO) {
        voucherService.update(voucherId, voucherDTO);
        Map<String, String> response = new HashMap<>();
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher is successfully updated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{voucherId}")
    @PreAuthorize("hasAuthority('ADMIN_DELETE')")
    public ResponseEntity<Map<String, String>> deleteVoucherById(@PathVariable String voucherId) {
        try {
            voucherService.delete(voucherId);
            Map<String, String> response = new HashMap<>();
            response.put("statusCode", "OK");
            response.put("statusMessage", String.format("Voucher with Id %s has been deleted", voucherId));
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", ex.getStatusCode().name());
            errorResponse.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PatchMapping("/{voucherId}/status")
    @PreAuthorize("hasAuthority('MANAGER_UPDATE')")
    public ResponseEntity<Map<String, String>> changeVoucherTourStatus(
            @PathVariable String voucherId,
            @RequestParam String status) {
        voucherService.changeTourStatus(voucherId, status);
        Map<String, String> response = new HashMap<>();
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher tour status successfully changed to " + status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cancel/{voucherId}")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<Map<String, String>> cancelVoucher(@PathVariable String voucherId, @RequestBody Map<String, String> request) {
        try {
            UUID userId = UUID.fromString(request.get("userId"));
            voucherService.cancelVoucher(voucherId, userId);

            Map<String, String> response = new HashMap<>();
            response.put("statusCode", "OK");
            response.put("statusMessage", "Voucher successfully canceled");

            return ResponseEntity.ok(response);
        } catch (IllegalStateException | EntityNotFoundException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", StatusCodes.BAD_REQUEST.name());
            errorResponse.put("statusMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", StatusCodes.INTERNAL_ERROR.name());
            errorResponse.put("statusMessage", "An error occurred while canceling the voucher");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PatchMapping("/{voucherId}/hot-status")
    @PreAuthorize("hasAuthority('MANAGER_UPDATE')")
    public ResponseEntity<Map<String, String>> changeHotStatus(
            @PathVariable String voucherId,
            @RequestParam boolean hotStatus) {
        voucherService.changeHotStatus(voucherId, hotStatus);
        Map<String, String> response = new HashMap<>();
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher hot status successfully changed to " + hotStatus);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<Map<String, Object>> filterVouchers(
            @RequestParam(required = false) String tourType,
            @RequestParam(required = false) String transferType,
            @RequestParam(required = false) String hotelType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<VoucherDTO> vouchers = voucherService.filterVouchers(tourType, transferType, hotelType, minPrice, maxPrice);
        Map<String, Object> response = new HashMap<>();
        response.put("results", vouchers);
        return ResponseEntity.ok(response);
    }
}
