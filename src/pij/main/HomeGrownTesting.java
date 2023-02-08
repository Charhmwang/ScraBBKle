package pij.main;


/**
 * Using "home-grown" testing framework for the designed classes.
 *
 * @author Haomeng Wang
 */

public class HomeGrownTesting {

    /** Index value for the next test. */
    private int testNo = 1;

    /** Number of passed tests. */
    private int passes = 0;

    /** Number of failed tests. */
    private int fails = 0;

    /** Output for successful test. */
    private static final String OKAY = "OK    ";

    /** Output for unsuccessful test. */
    private static final String FAIL = "FAILED";

    public void testBooleanEqual(String description, boolean expected, boolean actual) {
        this.sideEffectsForTest(description, expected + "", actual + "",
                expected == actual);
    }

    private void sideEffectsForTest(String description, String expected, String actual, boolean result) {
        String output;
        if (result) {
            this.passes++;
            output = OKAY;
        } else {
            this.fails++;
            output = FAIL;
        }

        System.out.println(output + " - Test " + this.testNo + ": " + description
                + ", expected: " + expected + ", actual: " + actual);
        this.testNo++;
    }

    /**
     * Main method that drives the tests.
     */
    public static void main(String[] args) {
        HomeGrownTesting tester = new HomeGrownTesting();
        tester.runTests();
    }

    public void runTests() {
        ValidateUserBoard testValidation1 = new ValidateUserBoard("../test_res/invalidBoard.txt");
        this.testBooleanEqual("ValidateUserBoard", false, testValidation1.test()); // 1

        ValidateUserBoard testValidation2 = new ValidateUserBoard("../resources/defaultBoard.txt");
        this.testBooleanEqual("ValidateUserBoard", true, testValidation2.test()); // 2
    }
}
