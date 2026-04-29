package com.gmmx.mvp.service;

import com.gmmx.mvp.core.tenant.TenantContext;
import com.gmmx.mvp.dto.ExpenseDtos;
import com.gmmx.mvp.entity.Expense;
import com.gmmx.mvp.exception.ResourceNotFoundException;
import com.gmmx.mvp.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Transactional
    public ExpenseDtos.ExpenseResponse createExpense(ExpenseDtos.ExpenseRequest request) {
        Expense expense = new Expense();
        mapToEntity(request, expense);
        return mapToResponse(expenseRepository.save(expense));
    }

    public List<ExpenseDtos.ExpenseResponse> getAllExpenses() {
        return expenseRepository.findAllByTenantIdOrderByDateDesc(TenantContext.getTenantId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseDtos.ExpenseResponse updateExpense(UUID id, ExpenseDtos.ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        mapToEntity(request, expense);
        return mapToResponse(expenseRepository.save(expense));
    }

    @Transactional
    public void deleteExpense(UUID id) {
        expenseRepository.deleteById(id);
    }

    private void mapToEntity(ExpenseDtos.ExpenseRequest request, Expense entity) {
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setAmount(request.getAmount());
        entity.setDate(request.getDate());
        entity.setCategory(request.getCategory());
        entity.setPaymentMethod(request.getPaymentMethod());
    }

    private ExpenseDtos.ExpenseResponse mapToResponse(Expense entity) {
        ExpenseDtos.ExpenseResponse response = new ExpenseDtos.ExpenseResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setAmount(entity.getAmount());
        response.setDate(entity.getDate());
        response.setCategory(entity.getCategory());
        response.setPaymentMethod(entity.getPaymentMethod());
        return response;
    }
}
