package com.dioni.financeiro.support;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public abstract class TestSupport {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        init();
    }

    protected void init() {
    }

    protected InOrder inOrder(Object... mocks) {
        return Mockito.inOrder(mocks);
    }
}