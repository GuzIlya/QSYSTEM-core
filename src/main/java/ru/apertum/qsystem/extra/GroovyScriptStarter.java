package ru.apertum.qsystem.extra;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Запускать скрипты на груви из папок "/plugins", "/plugins/groovy", "/groovy".
 */
@Log4j2
public class GroovyScriptStarter {

    private final String[] paths = {"./plugins", "./plugins/groovy", "./groovy"};
    private GroovyScriptEngine scriptEngine;

    private GroovyScriptStarter() {
        try {
            String str = Arrays.toString(paths);
            log.info("Init GroovyScriptEngine by {}.", str);
            scriptEngine = new GroovyScriptEngine(paths);
        } catch (IOException e) {
            log.error("Inition GroovyScriptEngine was failed.", e);
        }
    }

    public static GroovyScriptStarter get() {
        return StarterHolder.INSTANCE;
    }

    private static class StarterHolder {
        private static final GroovyScriptStarter INSTANCE = new GroovyScriptStarter();
    }

    /**
     * Выполняем скрипт груви.
     *
     * @param scriptName Имя скрипта. Это имя файла в папке со скриптами.
     * @param vars       Переменные для скрипта.
     * @param properties Параметры для скрипта.
     * @return false если скрипт не выполнился.
     */
    public Result run(String scriptName, Map<String, Object> vars, Map<String, Object> properties) {
        if (scriptEngine == null || scriptName == null) {
            log.warn("Groovy script '{}' will not invoke for vars={}; properties={}", scriptName, vars, properties);
            return null;
        } else {
            final Binding binding = new Binding();
            if (vars != null) {
                vars.forEach(binding::setVariable);
            }
            if (properties != null) {
                properties.forEach(binding::setProperty);
            }
            try {
                return new Result(scriptEngine.run(scriptName + ".groovy", binding), binding.getVariables());
            } catch (Exception e) {
                log.error("Invoke \"" + scriptName + "\" error. Vars: " + vars + "; Properties: " + properties, e);
                return null;
            }
        }
    }

    /**
     * Результат выполнения скрипта.
     */
    public static class Result {
        private final Object output;
        private final Map<String, Object> vars;

        public Result(Object output, Map<String, Object> vars) {
            this.output = output;
            this.vars = vars;
        }

        public Object getOutput() {
            return output;
        }

        public Map<String, Object> getVars() {
            return vars;
        }
    }
}
