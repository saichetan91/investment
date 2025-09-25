package Report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.*;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class ExtentReportListener implements ITestListener, ISuiteListener {

    private static final String REPORT_DIR = "reports";
    private static final String REPORT_FILE = REPORT_DIR + "/extent.html";
    private static ExtentReports extentReports;
    private static final ConcurrentHashMap<Long, ExtentTest> threadIdToTest = new ConcurrentHashMap<>();

    private static synchronized void ensureInitialized() {
        if (extentReports != null) {
            return;
        }

        new File(REPORT_DIR).mkdirs();

        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_FILE);
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle("Automation Report");
        spark.config().setReportName("Project Test Report");
        spark.config().setTimelineEnabled(true);

        extentReports = new ExtentReports();
        extentReports.attachReporter(spark);
        extentReports.setSystemInfo("Framework", "TestNG + RestAssured");
        extentReports.setSystemInfo("Java", System.getProperty("java.version"));
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
    }

    @Override
    public void onStart(ISuite suite) {
        ensureInitialized();
    }

    @Override
    public void onFinish(ISuite suite) {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        ensureInitialized();
        String className = result.getTestClass().getName();
        String method = result.getMethod().getMethodName();
        ExtentTest test = extentReports.createTest(className + "::" + method)
                .assignCategory(result.getTestContext().getName());
        threadIdToTest.put(Thread.currentThread().getId(), test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = threadIdToTest.get(Thread.currentThread().getId());
        if (test != null) test.pass("Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = threadIdToTest.get(Thread.currentThread().getId());
        if (test != null) test.fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = threadIdToTest.get(Thread.currentThread().getId());
        if (test != null) test.skip("Skipped");
    }

    @Override
    public void onStart(ITestContext context) {
        ensureInitialized();
    }

    @Override
    public void onFinish(ITestContext context) {
    }
}


