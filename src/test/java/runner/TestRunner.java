package runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/main/resources/features", //Indica o caminho dos arquivos "feature"
        glue = {"steps", "hook"}, // Indica o pacote onde os steps (passos) estão
        tags = "@regressivo", // Indica a tag que será executada
        plugin = {"html:target/cucumber-reports.html"} //Utilizada para gerar o arquivo HTML
)
public class TestRunner {
}
