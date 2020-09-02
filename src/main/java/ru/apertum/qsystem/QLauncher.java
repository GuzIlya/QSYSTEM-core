package ru.apertum.qsystem;

import lombok.NonNull;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.common.CodepagePrintStream;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QModule;
import ru.apertum.qsystem.common.Uses;

import java.awt.Font;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Locale;

import static ru.apertum.qsystem.common.Uses.ln;

/**
 * Запуск модулей кусистема. Загружает все настройки и ключи командной строки. Инициализирует лог. Делает всякое для подготовки старта модулей.
 * Тут логирование не использовать как @Log4j, т.к. еще не инициализировано.
 */
public final class QLauncher {

    private QLauncher() {
        // hide.
    }

    /**
     * Все старты любого модуля из main() вызывают этот лаунчер и передают сюда признак модуля.
     *
     * @param args   параметры командной строки.
     * @param module идентификатор стартующего модуля.
     */
    public static void run(String[] args, QModule module) {
        // установим логгер с учетом пришедших параметров из командной строке.
        Charset charset = StandardCharsets.UTF_8;
        if (!isPresent("-ide", args) && SystemUtils.IS_OS_WINDOWS) {
            charset = Charset.forName("cp866");
        }
        final String[] argsLog = {module.name(), charset.name()};
        MainMapLookup.setMainArguments(argsLog);
        final String logFile = getValue(args, "-log4j2-config", "--log4j2-config", "-logcfg");
        if (logFile != null) {
            final File file = Paths.get(logFile).toFile();
            Configurator.initialize(module.name(), file.exists() ?  file.getAbsolutePath() : "log4j2.xml");
        }

        // зпгрузим все настройки приложения.
        QConfig.cfg().prepareCLI(module, args);

        //Установка вывода консольных сообщений в нужной кодировке
        if (!QConfig.cfg().isIDE() && SystemUtils.IS_OS_WINDOWS) {
            try {
                String consoleEnc = System.getProperty("console.encoding", "Cp866");
                System.setOut(new CodepagePrintStream(System.out, consoleEnc));//NOSONAR
                System.setErr(new CodepagePrintStream(System.err, consoleEnc));//NOSONAR
            } catch (UnsupportedEncodingException e) {
                ln("Unable to setup console codepage: " + e);
            }
        }

        Locale.setDefault(Locales.getInstance().getLangCurrent());
        About.load();

        if (QConfig.cfg().getFont().isEmpty()) {
            switch (QConfig.cfg().getModule()) {
                case client:
                    Uses.setUIFont(Font.decode("Tahoma-Plain-16"));
                    break;
                case desktop:
                    Uses.setUIFont(Font.decode("Tahoma-Plain-20"));
                    break;
                case reception:
                    Uses.setUIFont(Font.decode("Tahoma-Plain-14"));
                    break;
                default:
                    Uses.setUIFont(Font.decode("Tahoma-Plain-12"));
            }
        } else {
            if (!"no".equalsIgnoreCase(QConfig.cfg().getFont())) {
                Uses.setUIFont(Font.decode(QConfig.cfg().getFont()));
            }
        }

        pause();
    }

    private static boolean isPresent(@NonNull String key, String[] args) {
        for (String arg : args) {
            if (key.equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }

    private static String getValue(String[] args, @NonNull String... keys) {
        for (int i = 0; i < args.length; i++) {
            for (int key = 0; key < keys.length; key++) {
                if (keys[key].equalsIgnoreCase(args[i]) && i < args.length - 1) {
                    final File log4j = new File(args[i + 1]);
                    if (log4j.exists()) {
                        return args[i + 1];
                    }
                }
            }
        }
        final File log4j = new File("config/log4j2.xml");
        if (log4j.exists()) {
            return log4j.getAbsolutePath();
        }
        return null;
    }

    private static void pause() {
        // ключ, отвечающий за паузу на старте.
        if (QConfig.cfg().getDelay() > 0) {
            try {
                Thread.sleep(QConfig.cfg().getDelay() * 1000L);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
