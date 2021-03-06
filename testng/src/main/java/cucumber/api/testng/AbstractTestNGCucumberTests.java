package cucumber.api.testng;

import cucumber.api.CucumberOptions;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import org.testng.annotations.Test;

public abstract class AbstractTestNGCucumberTests implements IHookable {
    private final TestNgReporter reporter;
    private final Runtime runtime;
    private final ClassLoader classLoader;
    private final ResourceLoader resourceLoader;

    public AbstractTestNGCucumberTests() {
        classLoader = getClass().getClassLoader();
        resourceLoader = new MultiLoader(classLoader);

        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(getClass(), new Class[]{CucumberOptions.class});
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();

        reporter = new TestNgReporter(System.out);
        runtimeOptions.getFormatters().add(reporter);
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
    }

    @Test(groups = "cucumber", description = "Runs Cucumber Features")
    public void run_cukes() {
        runtime.run();

        if (!runtime.getErrors().isEmpty()) {
            throw new CucumberException(runtime.getErrors().get(0));
        }
    }

    @Override
    public void run(IHookCallBack iHookCallBack, ITestResult iTestResult) {
        iHookCallBack.runTestMethod(iTestResult);
    }

}
