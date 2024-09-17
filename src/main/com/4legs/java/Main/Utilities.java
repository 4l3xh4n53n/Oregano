package Main;

import Commands.CommandType;
import Util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utilities {

    public static boolean isNumeric(String strNum) {
        if (strNum == null) return false;
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

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
