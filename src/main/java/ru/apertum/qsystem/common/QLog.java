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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Собственно, логер лог4Ж Это синглтон. Тут в место getInstance() для короткого написания используется l()
 *
 * @author Evgeniy Egorov
 * @deprecated по нормальному надо использовать логгер.
 */
@Deprecated
@Log4j2
public class QLog {

    private Logger logQUser;

    public Logger logger() {
        return log;
    }

    public static Logger log() {
        return log;
    }

    public Logger logQUser() {
        return logQUser;
    }

    public static Logger lgUser() {
        return l().logQUser();
    }

    private QLog() {
        logQUser = LogManager.getLogger(QModule.quser);
    }

    public static QLog l() {
        return LogerHolder.INSTANCE;
    }

    private static class LogerHolder {

        private static final QLog INSTANCE = new QLog();
    }
}
