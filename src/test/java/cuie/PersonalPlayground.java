package cuie;

import java.util.regex.Pattern;

public class PersonalPlayground {
    static final String FORMATTED_INTEGER_PATTERN = "%,d";
    private static final String INTEGER_REGEX = "[+-]?[\\d']{1,14}";
    private static final Pattern INTEGER_PATTERN = Pattern.compile(INTEGER_REGEX);


    static final String FORMATTED_DOUBLE_PATTERN = "%,.8f";
    private static final String DOUBLE_REGEX = "[+-]?[\\d]{1,2}[.]?[\\d]{0,8}";
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(DOUBLE_REGEX);

    public static void main(String[] args) {
        PersonalPlayground pp = new PersonalPlayground();
//        System.out.println(pp.convertToString(34.245));
//        System.out.println(pp.convertToString(234534.245));

        System.out.println(pp.isDouble("34.24"));
        System.out.println(pp.isInteger("34"));

    }

    private String convertToString(double newValue) {
        return String.format(FORMATTED_DOUBLE_PATTERN, newValue);
    }

    private boolean isInteger(String userInput) {
        return INTEGER_PATTERN.matcher(userInput).matches();
    }

    private boolean isDouble(String userInput) {
        return DOUBLE_PATTERN.matcher(userInput).matches();
    }

}
