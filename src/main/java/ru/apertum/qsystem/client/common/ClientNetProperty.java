/*
 *  Copyright (C) 2010 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.client.common;

import lombok.extern.log4j.Log4j2;
import ru.apertum.qsystem.common.model.IClientNetProperty;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Класс интерфейса INetPropertyImpl, для обработки командной строки клиентских модулей.
 *
 * @author Evgeniy Egorov
 */
@Log4j2
public class ClientNetProperty implements IClientNetProperty {

    private Integer portServer = -1; // Порт сервера
    private Integer portClient = -1; // Порт клиента
    private String adress; // Адрес сервера

    /**
     * По входным параметрам из консоли создадим объект с адресом и портом в сети.
     *
     * @param args параметры из консоли.
     * @deprecated всё в конфиге должно быть.
     */
    @Deprecated
    public ClientNetProperty(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("-sport".equalsIgnoreCase(args[i])) {
                portServer = Integer.parseInt(args[i + 1]);
            }
            if ("-cport".equalsIgnoreCase(args[i])) {
                portClient = Integer.parseInt(args[i + 1]);
            }
            if ("-s".equalsIgnoreCase(args[i])) {
                adress = args[i + 1];
            }
        }
    }

    /**
     * Создаем хранилище сетевых параметров.
     *
     * @param sport         порт сервера
     * @param cport         порт клиента.
     * @param serverAddress адрес сервера.
     */
    public ClientNetProperty(int sport, int cport, String serverAddress) {
        portServer = sport;
        portClient = cport;
        adress = serverAddress;
    }

    @Override
    public Integer getPort() {
        return portServer;
    }

    @Override
    public Integer getClientPort() {
        return portClient;
    }

    @Override
    public InetAddress getAddress() {
        InetAddress adr = null;
        try {
            adr = InetAddress.getByName(adress);
        } catch (UnknownHostException ex) {
            log.error("Error!", ex);
        }
        return adr;
    }
}
