package com.gmmx.mvp.service;

import com.gmmx.mvp.dto.TrainerDtos;
import com.gmmx.mvp.entity.TrainerProfile;
import com.gmmx.mvp.entity.UserAccount;
import com.gmmx.mvp.entity.UserRole;
import com.gmmx.mvp.repository.TrainerProfileRepository;
import com.gmmx.mvp.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final UserAccountRepository userAccountRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TrainerDtos.TrainerResponse createTrainer(TrainerDtos.TrainerCreateRequest request) {
        if (request.getEmail() != null && !request.getEmail().isEmpty() && userAccountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        UserAccount trainer = new UserAccount();
        trainer.setFullName(request.getFullName());
        trainer.setEmail(request.getEmail());
        trainer.setMobile(request.getMobile());
        trainer.setRole(UserRole.TRAINER);
        
        // Use provided PIN or fallback to last 4 digits of mobile
        String initialPin = request.getPin();
        if (initialPin == null || initialPin.isEmpty()) {
            if (request.getMobile() != null && request.getMobile().length() >= 4) {
                initialPin = request.getMobile().substring(request.getMobile().length() - 4);
            } else {
                initialPin = "1234"; // Absolute fallback
            }
        }
        trainer.setPasswordHash(passwordEncoder.encode(initialPin));
        
        // Ensure tenantId is set from context
        UUID tenantId = com.gmmx.mvp.core.tenant.TenantContext.getTenantId();
        trainer.setTenantId(tenantId);
        
        trainer = userAccountRepository.save(trainer);
        
        // Create TrainerProfile so trainer can be assigned to members
        TrainerProfile profile = new TrainerProfile();
        profile.setUser(trainer);
        profile.setTenantId(tenantId);
        trainerProfileRepository.save(profile);
        
        return mapToResponse(trainer);
    }

    @Transactional
    public TrainerDtos.TrainerResponse updateTrainer(UUID id, TrainerDtos.TrainerUpdateRequest request) {
        UserAccount trainer = userAccountRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.TRAINER)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        if (request.getFullName() != null) trainer.setFullName(request.getFullName());
        if (request.getEmail() != null) trainer.setEmail(request.getEmail());
        if (request.getMobile() != null) trainer.setMobile(request.getMobile());
        if (request.getActive() != null) trainer.setActive(request.getActive());

        trainer = userAccountRepository.save(trainer);
        return mapToResponse(trainer);
    }

    @Transactional
    public void deleteTrainer(UUID id) {
        UserAccount trainer = userAccountRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.TRAINER)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        // Also delete their TrainerProfile
        trainerProfileRepository.findByUserId(id).ifPresent(trainerProfileRepository::delete);
        userAccountRepository.delete(trainer);
    }

    @Transactional(readOnly = true)
    public Page<TrainerDtos.TrainerResponse> getAllTrainers(Pageable pageable) {
        return userAccountRepository.findByRole(UserRole.TRAINER, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public TrainerDtos.TrainerResponse updateTrainerPermissions(UUID trainerId, TrainerDtos.PermissionsUpdateRequest request) {
        // trainerId here is UserAccount.id
        TrainerProfile profile = trainerProfileRepository.findByUserId(trainerId)
                .orElseGet(() -> {
                    // Create profile if it doesn't exist (for trainers added before this fix)
                    UserAccount trainer = userAccountRepository.findById(trainerId)
                            .orElseThrow(() -> new RuntimeException("Trainer not found"));
                    TrainerProfile newProfile = new TrainerProfile();
                    newProfile.setUser(trainer);
                    newProfile.setTenantId(com.gmmx.mvp.core.tenant.TenantContext.getTenantId());
                    return trainerProfileRepository.save(newProfile);
                });

        String permissionsStr = request.getPermissions() != null
                ? String.join(",", request.getPermissions())
                : "";
        profile.setPermissions(permissionsStr);
        trainerProfileRepository.save(profile);
        return mapToResponse(profile.getUser());
    }

    private TrainerDtos.TrainerResponse mapToResponse(UserAccount account) {
        TrainerDtos.TrainerResponse response = new TrainerDtos.TrainerResponse();
        // Use UserAccount.id so frontend can pass it as assignedTrainerId
        // MemberService.findByUserId will resolve to TrainerProfile
        response.setId(account.getId());
        response.setFullName(account.getFullName());
        response.setEmail(account.getEmail());
        response.setMobile(account.getMobile());
        response.setActive(account.isEnabled());
        // Populate permissions from TrainerProfile
        trainerProfileRepository.findByUserId(account.getId()).ifPresent(profile ->
            response.setPermissions(profile.getPermissions())
        );
        return response;
    }
}
