package ru.apertum.qsystem;

import ru.apertum.qsystem.client.forms.FReception;
import ru.apertum.qsystem.common.QModule;

/**
 * Класс для старта админки зала.
 */
public class Reception {
    public static void main(String[] args) {
        QLauncher.run(args, QModule.reception);
        FReception.main();
    }
}
