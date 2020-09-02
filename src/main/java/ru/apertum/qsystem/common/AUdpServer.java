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
package ru.apertum.qsystem.common;

import lombok.extern.log4j.Log4j2;
import ru.apertum.qsystem.common.exceptions.ServerException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Сервер, принимающий сообщения по протоколу UDP.
 * Имеет абстрактный метод, который выполняется при получении сообщения.
 *
 * @author Evgeniy Egorov
 */
@Log4j2
public abstract class AUdpServer implements Runnable {

    /**
     * порт, который слушает сервер.
     */
    private final int port;
    private Thread thread;
    private DatagramSocket socket;
    private boolean isActive = true;

    public boolean isActivate() {
        return isActive;
    }

    public AUdpServer(int port) {
        this.port = port;
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        byte[] buffer = new byte[1024];

        log.trace("Старт UDP сервера на порту \"" + port + "\"");
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException ex) {
            isActive = false;
            throw new ServerException("Невозможно создать UDP-сокет на порту " + port + ". " + ex.getMessage());
        }
        while (!Thread.interrupted()) {
            isActive = true;
            //Receive request from client
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
            } catch (IOException ex) {
                isActive = false;
                if (Thread.interrupted()) {
                    throw new ServerException("Невозможно принять UDP-сообщение. " + ex.getMessage());
                }
            }
            InetAddress client = packet.getAddress();
            if (client != null) {// это когда закрывает прогу .. грязный хак
                int packetPort = packet.getPort();
                final String message = new String(buffer, packet.getOffset(), packet.getLength());
                log.trace("Приём UDP сообшение \"" + message + "\" ОТ адреса \"" + client.getHostName() + "\" с порта \"" + port + "\"");
                getData(message, client, packetPort);
            }
        }

    }

    /**
     * Обработка события получения сообщения.
     *
     * @param data          Текст сообщения
     * @param clientAddress адрес, откуда пришло сообщение
     * @param clientPort    порт, с которого послали сообщение
     */
    protected abstract void getData(String data, InetAddress clientAddress, int clientPort);

    /**
     * Остонавливаем сервер.
     */
    @SuppressWarnings("static-access")
    public void stop() {
        thread.interrupt();
        socket.close();
        log.trace("Остановка UDP сервера на порту \"" + port + "\"");
    }
}