package ru.apertum.qsystem;

import ru.apertum.qsystem.common.QModule;

/**
 * Класс для старта редактора табло.
 */
public class TabloRedactor {
    public static void main(String[] args) {
        QLauncher.run(args, QModule.tablo_redactor);
        ru.apertum.qsystem.client.TabloRedactor.main();
    }
}
