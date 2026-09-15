package doctor.backend.controller;

import doctor.backend.dto.owner.OwnerRequest;
import doctor.backend.dto.owner.OwnerResponse;
import doctor.backend.service.OwnerService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@CrossOrigin(origins = "http://localhost:5173")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(
            OwnerService ownerService
    ) {
        this.ownerService = ownerService;
    }

    // =====================================================
    // CREATE OWNER
    // POST /api/owners
    // =====================================================

    @PostMapping
    public ResponseEntity<OwnerResponse> createOwner(
            @Valid @RequestBody OwnerRequest request
    ) {

        OwnerResponse response =
                ownerService.createOwner(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // GET ALL OWNERS
    // GET /api/owners
    // =====================================================

    @GetMapping
    public ResponseEntity<List<OwnerResponse>> getAllOwners() {

        List<OwnerResponse> owners =
                ownerService.getAllOwners();

        return ResponseEntity.ok(owners);
    }

    // =====================================================
    // GET OWNER BY ID
    // GET /api/owners/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> getOwnerById(
            @PathVariable Long id
    ) {

        OwnerResponse owner =
                ownerService.getOwnerById(id);

        return ResponseEntity.ok(owner);
    }

    // =====================================================
    // UPDATE OWNER
    // PUT /api/owners/{id}
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<OwnerResponse> updateOwner(
            @PathVariable Long id,
            @Valid @RequestBody OwnerRequest request
    ) {

        OwnerResponse response =
                ownerService.updateOwner(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // DELETE OWNER
    // DELETE /api/owners/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(
            @PathVariable Long id
    ) {

        ownerService.deleteOwner(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}