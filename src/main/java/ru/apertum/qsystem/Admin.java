package ru.apertum.qsystem;

import ru.apertum.qsystem.client.forms.FAdmin;
import ru.apertum.qsystem.common.QModule;

/**
 * Класс для старта админки.
 */
public class Admin {
    public static void main(String[] args) throws Exception {
        QLauncher.run(args, QModule.admin);
        FAdmin.main();
    }
}
