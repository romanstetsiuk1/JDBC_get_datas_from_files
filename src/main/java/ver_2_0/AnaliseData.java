package ver_2_0;

public class AnaliseData {

    public static String convertDataToMySqlFormat(String toConvert) {
        if (toConvert.contains("January")) {
            return toConvert.replace("January", "01");
        }
        if (toConvert.contains("February")) {
            return toConvert.replace("February", "02");
        }
        if (toConvert.contains("March")) {
            return toConvert.replace("March", "03");
        }
        if (toConvert.contains("April")) {
            return toConvert.replace("April", "04");
        }
        if (toConvert.contains("May")) {
            return toConvert.replace("May", "05");
        }
        if (toConvert.contains("June")) {
            return toConvert.replace("June", "06");
        }
        if (toConvert.contains("July")) {
            return toConvert.replace("July", "07");
        }
        if (toConvert.contains("August")) {
            return toConvert.replace("August", "08");
        }
        if (toConvert.contains("September")) {
            return toConvert.replace("September", "09");
        }
        if (toConvert.contains("October")) {
            return toConvert.replace("October", "10");
        }
        if (toConvert.contains("November")) {
            return toConvert.replace("November", "11");
        }
        if (toConvert.contains("December")) {
            return toConvert.replace("December", "12");
        }
        return "";
    }

    public static String getActualDayValue(String actualDayValue, String currentLine) {
        String[] splitEndOfDay = currentLine.trim().split(" ");
        StringBuilder actualDayReport = new StringBuilder();
        for (int i = 3; i < splitEndOfDay.length; i++) {
            if (i == 5 && splitEndOfDay[i].length() == 1) {
                String tmpString = splitEndOfDay[i];
                splitEndOfDay[i] = "0" + tmpString;
            }
            actualDayReport.append(splitEndOfDay[i] + " ");
        }
        actualDayValue = actualDayReport.toString().trim()
                .replace(" ", "-");
        return actualDayValue;
    }

    public static boolean containsNumberValue(String line) {
        String[] numberValue = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (String checkLine : numberValue) {
            if (line.contains(checkLine)) {
                return true;
            }
        }
        return false;
    }

}
