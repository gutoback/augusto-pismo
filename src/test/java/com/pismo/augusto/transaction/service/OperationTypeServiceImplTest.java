package com.pismo.augusto.transaction.service;

import com.pismo.augusto.common.exception.NotFoundException;
import com.pismo.augusto.transaction.entity.OperationType;
import com.pismo.augusto.transaction.repository.OperationTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("test-unit")
class OperationTypeServiceImplTest {

    @Mock
    private OperationTypeRepository operationTypeRepository;

    @InjectMocks
    private OperationTypeServiceImpl operationTypeService;

    private OperationType operationType;
    private UUID operationTypeId;

    @BeforeEach
    void setUp() {
        operationTypeId = UUID.randomUUID();
        operationType = new OperationType();
        operationType.setId(operationTypeId);
        operationType.setDescription("DEBIT");
        operationType.setShouldNegateAmount(true);
    }

    @Test
    void testGetOperationTypeSuccess() {
        when(operationTypeRepository.findById(operationTypeId)).thenReturn(Optional.of(operationType));

        OperationType result = operationTypeService.getOperationType(operationTypeId);

        assertNotNull(result);
        assertEquals(operationTypeId, result.getId());
        assertEquals("DEBIT", result.getDescription());
        assertTrue(result.getShouldNegateAmount());
        verify(operationTypeRepository, times(1)).findById(operationTypeId);
    }

    @Test
    void testGetOperationTypeThrowsNotFoundWhenNotExists() {
        when(operationTypeRepository.findById(operationTypeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> operationTypeService.getOperationType(operationTypeId));
        verify(operationTypeRepository, times(1)).findById(operationTypeId);
    }

    @Test
    void testGetOperationTypeWithShouldNegateAmountFalse() {
        operationType.setShouldNegateAmount(false);
        when(operationTypeRepository.findById(operationTypeId)).thenReturn(Optional.of(operationType));

        OperationType result = operationTypeService.getOperationType(operationTypeId);

        assertFalse(result.getShouldNegateAmount());
    }

    @Test
    void testGetOperationTypes() {
        OperationType type1 = new OperationType();
        type1.setId(UUID.randomUUID());
        type1.setDescription("DEBIT");
        type1.setShouldNegateAmount(true);

        OperationType type2 = new OperationType();
        type2.setId(UUID.randomUUID());
        type2.setDescription("CREDIT");
        type2.setShouldNegateAmount(false);

        List<OperationType> operationTypes = Arrays.asList(type1, type2);
        when(operationTypeRepository.findAll()).thenReturn(operationTypes);

        List<OperationType> result = operationTypeService.getOperationTypes();

        assertEquals(2, result.size());
        verify(operationTypeRepository, times(1)).findAll();
    }

    @Test
    void testGetOperationTypesEmpty() {
        when(operationTypeRepository.findAll()).thenReturn(List.of());

        List<OperationType> result = operationTypeService.getOperationTypes();

        assertTrue(result.isEmpty());
        verify(operationTypeRepository, times(1)).findAll();
    }

    @Test
    void testGetOperationTypeWithDifferentDescriptions() {
        operationType.setDescription("CREDIT");
        when(operationTypeRepository.findById(operationTypeId)).thenReturn(Optional.of(operationType));

        OperationType result = operationTypeService.getOperationType(operationTypeId);

        assertEquals("CREDIT", result.getDescription());
    }
}
