package com.bavis.budgetapp.util;

import com.bavis.budgetapp.constants.TimeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
public class GeneralUtilTests {

    @Test
    public void testAddTimeToDate_Successful() {
        //Arrange
        LocalDateTime originalDateTime = LocalDateTime.of(2023, 6, 10, 10, 0, 0);
        TimeType timeType = TimeType.HOURS;

        //Act
        LocalDateTime updatedLocalDateTime = GeneralUtil.addTimeToDate(originalDateTime, 2, timeType);

        //Assert
        assertNotEquals(updatedLocalDateTime, originalDateTime);
        assertEquals(originalDateTime.plusHours(2), updatedLocalDateTime);
    }


    @Test
    public void testLocalTimeToDate_Successful() {
        //Arrange
        LocalDateTime now = LocalDateTime.now();
        Date expectedDate = new Date();

        //Act
        Date actualDate = GeneralUtil.localDateTimeToDate(now);

        //Assert
        assertEquals(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), actualDate.getTime());
    }
}
