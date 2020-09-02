package cu.tests;

import cu.core.CoreClass;
import io.cucumber.java.en.When;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Если;
import io.cucumber.java.ru.Иначе;
import io.cucumber.java.ru.Тогда;
import io.cucumber.java.en.Then;
import org.testng.Assert;

/**
 * {int}	Matches integers, for example 71 or -19.
 * {float}	Matches floats, for example 3.6, .8 or -9.2.
 * {word}	Matches words without whitespace, for example banana (but not banana split)
 * {string} Matches single-quoted or double-quoted strings, for example "banana split" or 'banana split' (but not banana split).
 * {} anonymous	Matches anything (/.* /).
 * On the JVM, there are additional parameter types for biginteger, bigdecimal, byte, short, long and double.
 * The anonymous parameter type will be converted to the parameter type of the step definition using an object mapper.
 * Cucumber comes with a built-in object mapper that can handle most basic types.
 * Aside from Enum it supports conversion to BigInteger, BigDecimal, Byte, Short, Integer, Long, Float, Double and String.
 */
public class CuTest {

    @Дано("накачаем мышцу/жопу {word} весом {int} кг")
    public void asIs(String mussel, int weight) {
        Assert.assertEquals(mussel, "бицуху");
        Assert.assertEquals(100, weight);
    }

    @Допустим("потренькаем {word}")
    public void go(String exersize) {
        Assert.assertEquals(exersize, "'становую'");
    }

    @Если("^на старт$")
    public void gogo() {
        Assert.assertTrue(new CoreClass().getBoolTrue());
    }

    @Тогда("^финиш$")
    public void tostop() {
        Assert.assertFalse(new CoreClass().getBoolFalse());
    }

    @Иначе("^не чемпион$")
    public void but() {
        Assert.assertFalse(new CoreClass().getBoolFalse() && new CoreClass().getBoolTrue());
    }

    @Then("qsystem version(s) {int}")
    public void ver(int ver) {
        Assert.assertNotEquals(201, ver);
    }

    @When("run qsystem")
    public void run() {
        Assert.assertFalse(new CoreClass().getBoolFalse() && new CoreClass().getBoolTrue());
    }
}
