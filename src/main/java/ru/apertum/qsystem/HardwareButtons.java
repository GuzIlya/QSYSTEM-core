package ru.apertum.qsystem;

import ru.apertum.qsystem.common.QModule;
import ru.apertum.qsystem.ub485.core.UBForm;

/**
 * Класс для старта сервера обработки хардварных кнопок управления очередями.
 */
public class HardwareButtons {
    public static void main(String[] args) {
        QLauncher.run(args, QModule.hardware_buttons);
        UBForm.main();
    }
}
