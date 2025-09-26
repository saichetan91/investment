import org.testng.TestNG;
import java.util.Arrays;

public class SuiteRunner {
    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            System.out.println("Running iteration: " + (i + 1));
            TestNG testng = new TestNG();
            testng.setTestSuites(Arrays.asList("SignupKYC.xml"));
            testng.run();
        }
    }
}