package ru.apertum.qsystem;

import ru.apertum.qsystem.common.QModule;
import ru.apertum.qsystem.server.QServer;

/**
 * Класс для старта сервера.
 */
public class Server {
    public static void main(String[] args) throws Exception {
        QLauncher.run(args, QModule.server);
        QServer.main();
    }
}
