package com.gmmx.mvp.service;

import com.gmmx.mvp.core.tenant.TenantContext;
import com.gmmx.mvp.dto.MemberDtos;
import com.gmmx.mvp.entity.MemberProfile;
import com.gmmx.mvp.entity.MembershipStatus;
import com.gmmx.mvp.entity.UserAccount;
import com.gmmx.mvp.entity.UserRole;
import com.gmmx.mvp.mapper.MemberMapper;
import com.gmmx.mvp.repository.MemberProfileRepository;
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
public class MemberService {

    private final UserAccountRepository userAccountRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberDtos.MemberResponse createMember(MemberDtos.MemberCreateRequest request) {
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 1. Create UserAccount
        UserAccount user = new UserAccount();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setMobile(com.gmmx.mvp.util.PhoneUtils.normalizeIdentifier(request.getMobile()));
        user.setRole(UserRole.MEMBER);
        user.setPasswordHash(passwordEncoder.encode(generateTemporaryPassword()));
        user = userAccountRepository.save(user);

        // 2. Create MemberProfile
        MemberProfile profile = new MemberProfile();
        profile.setUser(user);
        profile.setHeight(request.getHeight());
        profile.setWeight(request.getWeight());
        profile.setMedicalHistory(request.getMedicalHistory());
        profile.setGoals(request.getGoals());
        profile.setStatus(MembershipStatus.ACTIVE);
        profile = memberProfileRepository.save(profile);

        return memberMapper.toResponse(profile);
    }

    @Transactional(readOnly = true)
    public Page<MemberDtos.MemberResponse> getAllMembers(Pageable pageable) {
        return memberProfileRepository.findAll(pageable)
                .map(memberMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MemberDtos.MemberResponse getMemberById(UUID id) {
        return memberProfileRepository.findById(id)
                .map(memberMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    @Transactional
    public MemberDtos.MemberResponse updateMember(UUID id, MemberDtos.MemberUpdateRequest request) {
        MemberProfile profile = memberProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        UserAccount user = profile.getUser();
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getMobile() != null) user.setMobile(request.getMobile());
        
        userAccountRepository.save(user);

        if (request.getHeight() != null) profile.setHeight(request.getHeight());
        if (request.getWeight() != null) profile.setWeight(request.getWeight());
        if (request.getMedicalHistory() != null) profile.setMedicalHistory(request.getMedicalHistory());
        if (request.getGoals() != null) profile.setGoals(request.getGoals());
        if (request.getStatus() != null) profile.setStatus(request.getStatus());

        profile = memberProfileRepository.save(profile);
        return memberMapper.toResponse(profile);
    }

    @Transactional
    public void deleteMember(UUID id) {
        MemberProfile profile = memberProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        UserAccount user = profile.getUser();
        memberProfileRepository.delete(profile);
        userAccountRepository.delete(user);
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString() + UUID.randomUUID();
    }
}
