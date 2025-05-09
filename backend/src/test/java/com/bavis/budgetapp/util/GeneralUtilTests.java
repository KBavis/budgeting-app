package com.bavis.budgetapp.util;

import com.bavis.budgetapp.constants.TimeType;
import com.bavis.budgetapp.model.MonthYear;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
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
    void testIsDateInMonthAndYear_NullDateToCheck() {
        assertFalse(GeneralUtil.isDateInMonthAndYear(null, new MonthYear("March", 2024)));
    }

    @Test
    void testIsDateInMonthAndYear_NullMonthYear() {
        assertFalse(GeneralUtil.isDateInMonthAndYear(LocalDate.now(), null));
    }

    @Test
    void testIsDateInMonthAndYear_Valid() {
        MonthYear monthYear = MonthYear.builder()
                .month("March")
                .year(2024)
                .build();
        LocalDate localDate = LocalDate.of(2024, 3, 25);

        assertTrue(GeneralUtil.isDateInMonthAndYear(localDate, monthYear));
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

    @Test
    public  void testIsDateInPreviousMonth_Failure() {
        //Arrange date to be current month
        LocalDate now = LocalDate.now();
        LocalDate dateToTest = LocalDate.of(now.getYear(), now.getMonthValue(), 19);

        //Act
        boolean validity = GeneralUtil.isDateInPreviousMonth(dateToTest);

        //Assert
        assertFalse(validity);
    }

    @Test
    public  void testIsDateInPreviousMonth_Successful() {
        //Arrange date to be previous month
        LocalDate now = LocalDate.now();
        LocalDate dateToTest = LocalDate.of(now.getYear(), now.getMonthValue() - 1, 19);

        //Act
        boolean validity = GeneralUtil.isDateInPreviousMonth(dateToTest);

        //Assert
        assertTrue(validity);
    }

    @Test
    public  void testIsDateInCurrentMonth_Successful() {
        //Arrange
        LocalDate dateToTest = LocalDate.now();

        //Act
        boolean validity = GeneralUtil.isDateInCurrentMonth(dateToTest);

        //Assert
        assertTrue(validity);
    }

    @Test
    public void testIsDateInCurrentMonth_Failure() {
       //Arrange
       LocalDate dateToTest = LocalDate.of(2024, 4, 19);

       //Act
        boolean validity = GeneralUtil.isDateInCurrentMonth(dateToTest);

        //Assert
        assertFalse(validity);
    }

    @Test
    void testToNormalCase_NullWord_ReturnsEmpty() {
        String testInput = null;

        String output = GeneralUtil.toNormalCase(testInput);

        assertEquals("", output);
    }

    @Test
    void testToNormalCase_EmptyWord_ReturnsEmpty() {
        String testInput = "";

        String output = GeneralUtil.toNormalCase(testInput);

        assertEquals("", output);
    }

    @Test
    void testToNormalCase_MultipleWords_FirstLetterCapitalized() {
        String testInput = "THIS IS A PHRASE.";

        String output = GeneralUtil.toNormalCase(testInput);

        assertEquals("This is a phrase.", output);
    }

    @Test
    void testToNormalCase_SingleWord_FirstLetterCapitalized() {
        String testInput = "PHRASE";

        String output = GeneralUtil.toNormalCase(testInput);

        assertEquals("Phrase", output);
    }
}
