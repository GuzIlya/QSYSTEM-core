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
 * Перехватываемое исключение с логированием.
 *
 * @author Evgeniy Egorov
 */
@Log4j2
public class QException extends Exception {

    public QException(String textException) {
        super(textException);
        log.error("Exception!: " + textException);
    }

    public QException(String textException, Throwable tr) {
        super(textException, tr);
        log.error("Exception!: " + textException, tr);
    }
}
