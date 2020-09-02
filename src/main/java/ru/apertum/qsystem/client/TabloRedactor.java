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
package ru.apertum.qsystem.client;

import lombok.extern.log4j.Log4j2;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import ru.apertum.qsystem.client.forms.FBoardConfigJaxb;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.INetProperty;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;

/**
 * Старт редактора конфигурации клиентского табло.
 *
 * @author Evgeniy Egorov
 */
@Log4j2
public class TabloRedactor {

    private static File file;

    private TabloRedactor() {
        // hide
    }

    /**
     * Прога редактирования дизайна главного табло. Мышкой выставляем размеры и указываем настройки.
     */
    public static void main() {
        // проверить есть ли файл /config/clientboard.xml и если есть отправить его на редактирование
        if (QConfig.cfg().getTabloBoardCfgFile().isEmpty()) {
            throw new ServerException("No param '-tcfg' file for context.");
        }
        file = new File(QConfig.cfg().getTabloBoardCfgFile());
        if (!file.exists()) {
            throw new ServerException("File context \"" + QConfig.cfg().getTabloBoardCfgFile() + "\" not exist.");
        }
        log.info("Load file: " + file.getAbsolutePath());
        final SAXReader reader = new SAXReader(false);
        final Element root;
        try {
            root = reader.read(file).getRootElement();
        } catch (DocumentException ex) {
            throw new ServerException("Wrong xml file. " + ex.getMessage());
        }
        final FBoardConfigJaxbImpl bc = new FBoardConfigJaxbImpl(null, false, QConfig.cfg().getClientNetProperty());
        bc.setTitle(bc.getTitle() + " " + file.getAbsolutePath());
        bc.setParams(root);
        Uses.setLocation(bc);
        bc.setVisible(true);
    }

    static class FBoardConfigJaxbImpl extends FBoardConfigJaxb {
        FBoardConfigJaxbImpl(JFrame parent, boolean modal, INetProperty netProperty) {
            super(parent, modal);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.netProperty = netProperty;
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    System.exit(0);
                }
            });
        }

        @Override
        public void saveResult() {
            saveForm();
            saveToFile();
            JOptionPane.showMessageDialog(this, "Сохранение завершено успешно.", "Сохранение", JOptionPane.INFORMATION_MESSAGE);
        }

        @Override
        public void hideRedactor() {
            super.hideRedactor();
            System.exit(0);
        }

        /**
         * Сохранение состояния настроек клиентского табло в xml-файл на диск.
         */
        void saveToFile() {
            final long start = System.currentTimeMillis();
            log.info("Сохранение состояния. To file: " + file.getAbsolutePath());
            try {
                Files.write(file.toPath(), getBoard().marshalWithCData());
                Uses.ln("Success!");
            } catch (Exception ex) {
                log.error("FAILED...", ex);
            }
            log.info("Состояние сохранено. Затрачено времени: " + ((double) (System.currentTimeMillis() - start)) / 1000 + " сек.");
        }
    }
}
