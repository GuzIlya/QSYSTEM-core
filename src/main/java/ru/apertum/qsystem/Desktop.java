package ru.apertum.qsystem;

import ru.apertum.qsystem.common.QModule;

/**
 * Класс для старта тачевого клиента для "бургерной".
 */
public class Desktop {
    public static void main(String[] args) {
        QLauncher.run(args, QModule.desktop);
        ru.apertum.qsystem.client.fx.Desktop.main();
    }
}
