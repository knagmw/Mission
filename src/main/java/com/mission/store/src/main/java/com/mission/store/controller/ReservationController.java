package com.mission.store.controller;

import com.mission.store.domain.Store;
import com.mission.store.dto.ReservationDto;
import com.mission.store.dto.ReservationRegistration;
import com.mission.store.dto.StoreDto;
import com.mission.store.exception.StoreException;
import com.mission.store.repository.StoreRepository;
import com.mission.store.service.ReservationService;
import com.mission.store.type.ReservationApprovalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mission.store.type.ErrorCode.INVALID_STORE_ID;

@RequestMapping("/store/api/v1")
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final StoreRepository storeRepository;

    /** 예약 요청 */
    @PostMapping("/reservations")
    public ResponseEntity<?> reserve(
            @Valid @RequestBody ReservationRegistration.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.reserve(request));
    }

    /** 예약 취소 */
    @PutMapping("/reservations/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }
    
    /** 키오스크 예약 방문 확인 */
    @PutMapping("/reservations/{id}/kiosk/visit")
    public ResponseEntity<?> confirmVisit(
            @PathVariable Long id
            , @RequestParam("reservationCode") String reservationCode) {
        reservationService.confirmVisit(id, reservationCode);
        return ResponseEntity.ok().build();
    }

    /** 매장 점주가 매장의 매장 정보와 해당 매장의 모든 예약 확인 */
    @GetMapping("/stores/{id}/reservations")
    public ResponseEntity<?> getReservationsByStoreId(@PathVariable Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreException(INVALID_STORE_ID));
        StoreDto storeDto = StoreDto.fromEntity(store);
        List<ReservationDto> reservations = reservationService.getReservationsByStoreId(id);

        Map<String, Object> response = new HashMap<>();
        response.put("store", storeDto);
        response.put("reservations", reservations);
        return ResponseEntity.ok().body(response);
    }

    /** 매장 점주가 예약 승인 및 거절 */
    @PostMapping("/reservations/{id}/approval")
    public ResponseEntity<String> approveOrRejectReservation(
            @PathVariable Long id
            , @RequestParam ReservationApprovalStatus status) {
        reservationService.approveOrRejectReservation(id, status);
        return ResponseEntity.ok().build();
    }
}
