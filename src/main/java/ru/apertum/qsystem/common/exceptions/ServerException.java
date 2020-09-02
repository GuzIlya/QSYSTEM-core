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
package ru.apertum.qsystem.common.exceptions;

import lombok.extern.log4j.Log4j2;

/**
 * Этот класс исключения использовать для програмной генерации исклюсений.
 * Записывает StackTrace и  само исключение в лог.
 * Это исключение не показывает диологовое окно при возникновении ошибки
 * Используется в системе статистики и отчетов.
 *
 * @author Evgeniy Egorov
 */
@Log4j2
public class ServerException extends RuntimeException {

    public ServerException(String textException) {
        super(textException);
        log.error("Error! " + textException, this);
    }

    public ServerException(Exception ex) {
        super(ex);
        log.error("Error! " + ex.toString(), this);
    }

    public ServerException(String textException, Exception ex) {
        super(textException, ex);
        log.error("Error! " + textException + "\n" + ex.toString(), this);
    }
}
