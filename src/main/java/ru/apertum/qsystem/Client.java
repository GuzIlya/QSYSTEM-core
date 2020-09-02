package ru.apertum.qsystem;

import ru.apertum.qsystem.client.forms.FClient;
import ru.apertum.qsystem.common.QModule;

/**
 * Класс для старта приложения оператора.
 */
public class Client {
    public static void main(String[] args) {
        QLauncher.run(args, QModule.client);
        FClient.main();
    }
}
