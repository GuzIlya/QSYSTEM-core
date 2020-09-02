package cu;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;

import static io.cucumber.testng.CucumberOptions.SnippetType.CAMELCASE;

@CucumberOptions(
        features = {"src/test/resources/cu"},
        glue = {"cu.core", "cu.tests"},
        snippets = CAMELCASE
)
@Test
public class CuGo extends AbstractTestNGCucumberTests {
}
