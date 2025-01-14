package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.exception.StatusCodes;
import com.epam.finaltask.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    private static final Logger logger = LoggerFactory.getLogger(VoucherController.class);

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<Map<String, Object>> findAll(Locale locale) {
        logger.info("Received request to find all vouchers.");
        List<VoucherDTO> vouchers = voucherService.findAll();
        logger.info("Found {} vouchers.", vouchers.size());
        Map<String, Object> response = new HashMap<>();
        response.put("results", vouchers);
        response.put("statusMessage", messageSource.getMessage("voucher.find.all.success", null, locale));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<Map<String, Object>> findAllByUserId(@PathVariable String userId, Locale locale) {
        logger.info("Received request to find all vouchers for userId: {}.", userId);
        List<VoucherDTO> vouchers = voucherService.findAllByUserId(userId);
        logger.info("Found {} vouchers for userId: {}.", vouchers.size(), userId);
        Map<String, Object> response = new HashMap<>();
        response.put("results", vouchers);
        response.put("statusMessage", messageSource.getMessage("voucher.find.by.user.success", null, locale));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/voucher/{voucherId}")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<VoucherDTO> findVoucherById(@PathVariable String voucherId, Locale locale) {
        logger.info("Received request to find voucher by id: {}.", voucherId);
        VoucherDTO voucher = voucherService.findById(voucherId);
        logger.info("Voucher found: {}.", voucher.getId());
        return ResponseEntity.ok(voucher);
    }

    @PostMapping("/order/{voucherId}")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<?> orderVoucher(@PathVariable String voucherId, @RequestBody Map<String, String> request, Locale locale) {
        logger.info("Received request to order voucher with id: {}.", voucherId);
        try {
            UUID userId = UUID.fromString(request.get("userId"));
            VoucherDTO updatedVoucher = voucherService.orderVoucher(voucherId, userId);
            logger.info("Voucher successfully ordered: {}.", updatedVoucher.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", messageSource.getMessage("voucher.order.success", null, locale));
            response.put("data", updatedVoucher);

            return ResponseEntity.ok(response);
        } catch (IllegalStateException | EntityNotFoundException ex) {
            logger.error("Error ordering voucher: {}.", ex.getMessage(), ex);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.BAD_REQUEST.name());
            response.put("statusMessage", messageSource.getMessage("voucher.order.error", new Object[]{ex.getMessage()}, locale));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception ex) {
            logger.error("Unexpected error while ordering voucher: {}.", ex.getMessage(), ex);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCodes.INTERNAL_ERROR.name());
            response.put("statusMessage", messageSource.getMessage("voucher.order.unexpected.error", null, locale));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/allVouchers")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<Map<String, Object>> findAllVouchers() {
        logger.info("Received request to find all vouchers");
        List<VoucherDTO> vouchers = voucherService.findAll();
        logger.info("Found {} vouchers.", vouchers.size());
        Map<String, Object> response = new HashMap<>();
        response.put("results", vouchers);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    public ResponseEntity<Map<String, String>> createVoucher(@Valid @RequestBody VoucherDTO voucherDTO, Locale locale) {
        logger.info("Received request to create a new voucher with id: {}.", voucherDTO.getId());
        voucherService.create(voucherDTO);
        logger.info("Voucher successfully created.");
        Map<String, String> response = new HashMap<>();
        response.put("statusCode", StatusCodes.OK.name());
        response.put("statusMessage", messageSource.getMessage("voucher.create.success", null, locale));
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/change/{voucherId}")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<Map<String, String>> updateVoucher(@PathVariable String voucherId, @Valid @RequestBody VoucherDTO voucherDTO, Locale locale) {
        logger.info("Received request to update voucher with id: {}.", voucherId);
        voucherService.update(voucherId, voucherDTO);
        logger.info("Voucher successfully updated.");
        Map<String, String> response = new HashMap<>();
        response.put("statusCode", StatusCodes.OK.name());
        response.put("statusMessage", messageSource.getMessage("voucher.update.success", null, locale));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{voucherId}")
    @PreAuthorize("hasAuthority('ADMIN_DELETE')")
    public ResponseEntity<Map<String, String>> deleteVoucherById(@PathVariable String voucherId, Locale locale) {
        logger.info("Received request to delete voucher with id: {}.", voucherId);
        try {
            voucherService.delete(voucherId);
            logger.info("Voucher with id {} successfully deleted.", voucherId);
            Map<String, String> response = new HashMap<>();
            response.put("statusCode", StatusCodes.OK.name());
            response.put("statusMessage", messageSource.getMessage("voucher.delete.success", new Object[]{voucherId}, locale));
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            logger.error("Error deleting voucher: {}.", ex.getMessage(), ex);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", ex.getStatusCode().name());
            errorResponse.put("statusMessage", messageSource.getMessage("voucher.delete.error", new Object[]{ex.getMessage()}, locale));
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PatchMapping("/{voucherId}/status")
    @PreAuthorize("hasAuthority('MANAGER_UPDATE')")
    public ResponseEntity<Map<String, String>> changeVoucherTourStatus(
            @PathVariable String voucherId,
            @RequestParam String status,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        logger.info("Received request to change tour status of voucher with id: {} to {}.", voucherId, status);
        voucherService.changeTourStatus(voucherId, status);
        logger.info("Voucher tour status successfully changed to {}.", status);
        Map<String, String> response = new HashMap<>();
        response.put("statusCode", "OK");
        response.put("statusMessage", messageSource.getMessage("voucher.status.changed", new Object[]{status}, locale));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cancel/{voucherId}")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<Map<String, String>> cancelVoucher(
            @PathVariable String voucherId,
            @RequestBody Map<String, String> request,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        logger.info("Received request to cancel voucher with id: {}.", voucherId);
        try {
            UUID userId = UUID.fromString(request.get("userId"));
            voucherService.cancelVoucher(voucherId, userId);
            logger.info("Voucher with id {} successfully canceled for userId: {}.", voucherId, userId);

            Map<String, String> response = new HashMap<>();
            response.put("statusCode", "OK");
            response.put("statusMessage", messageSource.getMessage("voucher.canceled", null, locale));

            return ResponseEntity.ok(response);
        } catch (IllegalStateException | EntityNotFoundException ex) {
            logger.error("Error canceling voucher: {}.", ex.getMessage(), ex);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", StatusCodes.BAD_REQUEST.name());
            errorResponse.put("statusMessage", messageSource.getMessage("voucher.error.cancel", new Object[]{ex.getMessage()}, locale));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            logger.error("Unexpected error while canceling voucher: {}.", ex.getMessage(), ex);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", StatusCodes.INTERNAL_ERROR.name());
            errorResponse.put("statusMessage", messageSource.getMessage("voucher.error.internal", null, locale));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PatchMapping("/{voucherId}/hot-status")
    @PreAuthorize("hasAuthority('MANAGER_UPDATE')")
    public ResponseEntity<Map<String, String>> changeHotStatus(
            @PathVariable String voucherId,
            @RequestParam boolean hotStatus,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        logger.info("Request received to change hot status for voucherId: {}, hotStatus: {}", voucherId, hotStatus);
        try {
            voucherService.changeHotStatus(voucherId, hotStatus);
            Map<String, String> response = new HashMap<>();
            response.put("statusCode", "OK");
            response.put("statusMessage", messageSource.getMessage("voucher.hot.status.changed", new Object[]{hotStatus}, locale));
            logger.info("Hot status successfully changed for voucherId: {}, new status: {}", voucherId, hotStatus);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error while changing hot status for voucherId: {}, error: {}", voucherId, ex.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", StatusCodes.INTERNAL_ERROR.name());
            errorResponse.put("statusMessage", messageSource.getMessage("voucher.error.internal", null, locale));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ADMIN_READ') or hasAuthority('MANAGER_READ')")
    public ResponseEntity<Map<String, Object>> filterVouchers(
            @RequestParam(required = false) String tourType,
            @RequestParam(required = false) String transferType,
            @RequestParam(required = false) String hotelType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        logger.info("Request received to filter vouchers with parameters - tourType: {}, transferType: {}, hotelType: {}, minPrice: {}, maxPrice: {}",
                tourType, transferType, hotelType, minPrice, maxPrice);
        try {
            List<VoucherDTO> vouchers = voucherService.filterVouchers(tourType, transferType, hotelType, minPrice, maxPrice);
            Map<String, Object> response = new HashMap<>();
            response.put("results", vouchers);
            logger.info("Vouchers successfully filtered. Total results: {}", vouchers.size());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error while filtering vouchers. Error: {}", ex.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", StatusCodes.INTERNAL_ERROR.name());
            errorResponse.put("statusMessage", messageSource.getMessage("voucher.error.internal", null, locale));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
