package com.gmmx.mvp.service;

import com.gmmx.mvp.dto.TrainerDtos;
import com.gmmx.mvp.entity.UserAccount;
import com.gmmx.mvp.entity.UserRole;
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
        trainer.setPasswordHash(passwordEncoder.encode(generateTemporaryPassword()));
        
        trainer = userAccountRepository.save(trainer);
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
        userAccountRepository.delete(trainer);
    }

    @Transactional(readOnly = true)
    public Page<TrainerDtos.TrainerResponse> getAllTrainers(Pageable pageable) {
        return userAccountRepository.findByRole(UserRole.TRAINER, pageable)
                .map(this::mapToResponse);
    }

    private TrainerDtos.TrainerResponse mapToResponse(UserAccount account) {
        TrainerDtos.TrainerResponse response = new TrainerDtos.TrainerResponse();
        response.setId(account.getId());
        response.setFullName(account.getFullName());
        response.setEmail(account.getEmail());
        response.setMobile(account.getMobile());
        response.setActive(account.isEnabled());
        return response;
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString() + UUID.randomUUID();
    }
}
