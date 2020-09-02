package ru.apertum.qsystem.extra;

import net.sf.jasperreports.engine.util.JsonUtil;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public class GroovyScriptStarterTest {

    @Test
    public void testRunMissingPropertyException() {
        GroovyScriptStarter.Result result = GroovyScriptStarter.get().run("unitTest", null, null);
        assertNull(result);
    }

    @Test
    public void testRunVarProps() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("var1", "str");
        vars.put("var2", 1L);
        String[] v3 = {"a1", "a2", "a3"};
        vars.put("var3", v3);
        Map<String, Object> properties = new HashMap<>();
        properties.put("p1", "prop");
        properties.put("p2", 100L);
        properties.put("p3", new String[]{"p1", "p2", "p3"});

        GroovyScriptStarter.Result result = GroovyScriptStarter.get().run("unitTest", vars, properties);
        assertEquals(result.getOutput(), "test success!");

        assertEquals(result.getVars().get("v1"), "str");
        assertEquals(result.getVars().get("v2"), 1L);
        assertEquals(result.getVars().get("v3"), v3);
        assertEquals(result.getVars().get("v_str"), "some str");
        assertEquals(result.getVars().get("v_int"), 200);
        assertEquals(result.getVars().get("p1"), "new P1");

        properties.put("p1", "not prop");
        result = GroovyScriptStarter.get().run("unitTest", vars, properties);
        assertEquals(result.getOutput(), "p1 != prop");

        properties.put("p1", "prop");
        properties.put("p2", 2);
        result = GroovyScriptStarter.get().run("unitTest", vars, properties);
        assertEquals(result.getOutput(), 1L);
    }
}