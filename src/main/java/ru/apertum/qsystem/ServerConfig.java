package ru.apertum.qsystem;

import ru.apertum.qsystem.client.forms.FServerConfig;
import ru.apertum.qsystem.common.QModule;

/**
 * Класс для старта настройки БД сервера.
 */
public class ServerConfig {
    public static void main(String[] args) {
        QLauncher.run(args, QModule.server);
        FServerConfig.main();
    }
}
