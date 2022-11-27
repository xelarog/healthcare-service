package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicalServiceImplTests {

    PatientInfoRepository patientInfoRepository;
    SendAlertService sendAlertService;
    ArgumentCaptor<String> argumentCaptor;
    MedicalService medicalService;

    @BeforeEach
    public void initialize() {
        patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        sendAlertService = Mockito.mock(SendAlertService.class);
        argumentCaptor = ArgumentCaptor.forClass(String.class);
        medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        Mockito.when(patientInfoRepository.getById("id1"))
                .thenReturn(new PatientInfo("id1", "Сергей", "Иванов",
                        LocalDate.of(1976, 5, 19),
                        new HealthInfo(new BigDecimal("36.65"),
                                new BloodPressure(125, 85))));
    }

    @Test
    public void checkBloodPressureTest() {

        medicalService.checkBloodPressure("id1", new BloodPressure(110, 95));

        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: id1, need help", argumentCaptor.getValue());
    }

    @Test
    public void checkTemperatureTest() {

        medicalService.checkTemperature("id1", new BigDecimal("40"));

        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: id1, need help", argumentCaptor.getValue());
    }

    @Test
    public void normalHealthInfoTest() {

        medicalService.checkTemperature("id1", new BigDecimal("36.7"));
        medicalService.checkBloodPressure("id1", new BloodPressure(125, 85));

        Mockito.verify(sendAlertService, Mockito.times(0)).send(Mockito.any());
    }

}