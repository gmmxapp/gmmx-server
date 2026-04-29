package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.ExpenseDtos;
import com.gmmx.mvp.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Admin - Expenses", description = "Gym expense management")
@SecurityRequirement(name = "BearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Log a new expense")
    public ApiResponse<ExpenseDtos.ExpenseResponse> create(@Valid @RequestBody ExpenseDtos.ExpenseRequest request) {
        return ApiResponse.success(expenseService.createExpense(request), "Expense logged successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "List all gym expenses")
    public ApiResponse<List<ExpenseDtos.ExpenseResponse>> getAll() {
        return ApiResponse.success(expenseService.getAllExpenses(), "Expenses retrieved successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Update an expense record")
    public ApiResponse<ExpenseDtos.ExpenseResponse> update(@PathVariable UUID id, @Valid @RequestBody ExpenseDtos.ExpenseRequest request) {
        return ApiResponse.success(expenseService.updateExpense(id, request), "Expense updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Delete an expense record")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        expenseService.deleteExpense(id);
        return ApiResponse.success(null, "Expense deleted successfully");
    }
}
