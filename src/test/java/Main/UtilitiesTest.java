package Main;

import Util.Tuple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilitiesTest {

    @Test
    void isNumericWithInteger() {
        assertTrue(Utilities.isNumeric("892734"));
    }

    @Test
    void isNumericWithDecimal() {
        assertTrue(Utilities.isNumeric("892.987"));
    }

    @Test
    void isNumericWithString() {
        assertFalse(Utilities.isNumeric("fortnite"));
    }

    @Test
    void isNumericWithEmptyString() {
        assertFalse(Utilities.isNumeric(""));
    }

    @Test
    void isNumericWithNull() {
        assertFalse(Utilities.isNumeric(null));
    }

    @Test
    void createFilterCorrectType() {
        String[] expected = new String[]{"ADMINISTRATIVE"};
        String[] result = Utilities.createFilter(new String[]{"Administrative"}, 0).x();
        assertArrayEquals(expected, result);
    }

    @Test
    void createFilterIncorrectType() {
        String[] expected = new String[]{"N/A"};
        String[] result = Utilities.createFilter(new String[]{"N/A"}, 0).y();
        assertArrayEquals(expected, result);
    }

    @Test
    void createFilterMixed() {
        String[] expected = new String[]{"ADMINISTRATIVE"};
        String[] expectedUnknown = new String[]{"N/A"};
        Tuple<String[], String[]> result = Utilities.createFilter(new String[]{"Administrative", "N/A"}, 0);

        assertArrayEquals(expected, result.x());
        assertArrayEquals(expectedUnknown, result.y());
    }

    @Test
    void createFilterMixedWithExtraArg() {

        String[] expected = new String[]{"ADMINISTRATIVE"};
        String[] expectedUnknown = new String[]{"N/A"};
        Tuple<String[], String[]> result = Utilities.createFilter(new String[]{"Extra command argument", "Administrative", "N/A"}, 1);

        assertArrayEquals(expected, result.x());
        assertArrayEquals(expectedUnknown, result.y());
    }

}
