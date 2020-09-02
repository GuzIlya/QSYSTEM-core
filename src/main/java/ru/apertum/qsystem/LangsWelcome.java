package ru.apertum.qsystem;

import ru.apertum.qsystem.client.forms.FLangsOnWelcome;
import ru.apertum.qsystem.common.QModule;

/**
 * Класс для старта настройки количества языков интерфейса для пункта регистрации.
 */
public class LangsWelcome {
    public static void main(String[] args) throws InterruptedException {
        QLauncher.run(args, QModule.welcome);
        FLangsOnWelcome.main();
    }
}
