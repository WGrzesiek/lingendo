package com.learnwords.deckservice;

import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.service.Algorithm.AbstractAlgorithm;
import com.learnwords.deckservice.service.Algorithm.AlgorithmFactory;
import com.learnwords.deckservice.service.Algorithm.GrzesiekAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlgorithmFactoryTest {

    @Mock
    private GrzesiekAlgorithm grzesiekAlgorithm;

    private AlgorithmFactory algorithmFactory;

    @BeforeEach
    public void setUp() {
        when(grzesiekAlgorithm.getType()).thenReturn(LearnAlgorithm.GRZESIEK_ALGORITHM);

        List<AbstractAlgorithm> algorithms = List.of(grzesiekAlgorithm);
        algorithmFactory = new AlgorithmFactory(algorithms);
    }

    @Test
    public void shouldReturnCorrectAlgorithmForGrzesiekType() {
        AbstractAlgorithm result = algorithmFactory.get(LearnAlgorithm.GRZESIEK_ALGORITHM);
        assertEquals(grzesiekAlgorithm, result);
    }

    @Test
    public void shouldThrowExceptionForUnsupportedAlgorithmType() {
        assertThrows(IllegalArgumentException.class,
                () -> algorithmFactory.get(LearnAlgorithm.TEST_ALGORITHM));
    }


}
