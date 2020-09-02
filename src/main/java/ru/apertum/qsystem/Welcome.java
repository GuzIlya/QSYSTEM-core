package ru.apertum.qsystem;

import ru.apertum.qsystem.client.forms.FWelcome;
import ru.apertum.qsystem.common.QModule;

/**
 * Класс для старта пункта регистрации на киоске.
 */
public class Welcome {
    public static void main(String[] args) throws Exception {
        QLauncher.run(args, QModule.welcome);
        FWelcome.main();
    }
}
