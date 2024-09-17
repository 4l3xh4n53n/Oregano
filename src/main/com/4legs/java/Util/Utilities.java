package Util;

import Commands.CommandType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utilities {

    /**
     * Checks if a given string is a numeric value
     * @param strNum (String) to check
     * @return true if string is numeric, true otherwise
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) return false;
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Creates a filter of command types from an array of arguments
     * @param args      (String[]) command arguments
     * @param argStart  (int) where the first filter argument starts in (String[]) args
     * @return Tuple ( filter, unknown command types )
     */
    public static Tuple<String[], String[]> createFilter(String[] args, int argStart) {
        List<String> expectedCommandTypes = Arrays.stream(CommandType.values()).map(Enum::name).toList();
        List<String> filter = new ArrayList<>(); // Filters display by command type
        List<String> unknownFilterTypes = new ArrayList<>(); // Used to display invalid input from user

        for (int i = argStart; i < args.length; i++) {

            // Checks that argument is a valid type of feature, e.g. ADMINISTRATIVE,
            String filterItem = args[i].toUpperCase();
            if (expectedCommandTypes.contains(filterItem)) {
                filter.add(filterItem);
            } else {
                unknownFilterTypes.add(filterItem);
            }
        }

        String[] arrFilter = filter.toArray(String[]::new);
        String[] arrUnknownFilterTypes = unknownFilterTypes.toArray(String[]::new);

        return new Tuple<>(arrFilter, arrUnknownFilterTypes);
    }

}
