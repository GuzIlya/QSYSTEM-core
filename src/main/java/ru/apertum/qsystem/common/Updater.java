/*
 * Copyright (C) 2017 Apertum Project LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.common;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.util.Pair;
import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.exception.ZipException;
import ru.apertum.qsystem.common.exceptions.QException;
import ru.apertum.qsystem.common.exceptions.ServerException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Механизм обновления по сети и распаковки.
 *
 * @author Evgeniy Egorov
 */
@Log4j2
public class Updater {

    private Updater() {
    }

    @Expose
    @SerializedName("updates")
    private final LinkedList<Update> updates = new LinkedList<>();

    public static class Update {

        @Expose
        @SerializedName("status")
        private String status = "new";

        @Expose
        @SerializedName("url")
        private String url;

        @Expose
        @SerializedName("checkTime")
        private Date checkTime;

        @Expose
        @SerializedName("md5")
        private String md5;

        @Expose
        @SerializedName("size")
        private Long size;

        @Expose
        @SerializedName("lastModified")
        private Long lastModified;

        @Expose
        @SerializedName("file")
        private String file;

        @Expose
        @SerializedName("downloadedTime")
        private Date downloadedTime;

        @Expose
        @SerializedName("unzippedTime")
        private Date unzippedTime;
    }

    /**
     * Сохраним во временный файл.
     */
    public void save() {
        // в темповый файл
        final Gson gson = GsonPool.getInstance().borrowGson();
        try (final FileOutputStream fos = new FileOutputStream(new File(Uses.CONFIG_FOLDER + File.separator + Uses.TEMP_UPDLOG_FILE))) {
            fos.write(gson.toJson(this).getBytes(StandardCharsets.UTF_8));
            fos.flush();
        } catch (FileNotFoundException ex) {
            throw new ServerException("Не возможно создать файл состояния обновления. " + ex.getMessage());
        } catch (IOException ex) {
            throw new ServerException("Не возможно сохранить изменения в поток." + ex.getMessage());
        }
    }

    /**
     * Получаем инстанс закачивальщика. На диск
     *
     * @return этим апдейтером.
     */
    public static Updater load() {
        final Updater updLog;
        final File recovFile = new File(Uses.CONFIG_FOLDER + File.separator + Uses.TEMP_UPDLOG_FILE);
        if (recovFile.exists()) {
            final StringBuilder recData = new StringBuilder();
            try (final FileInputStream fis = new FileInputStream(recovFile)) {
                try (final Scanner scan = new Scanner(fis, StandardCharsets.UTF_8.name())) {
                    while (scan.hasNextLine()) {
                        recData.append(scan.nextLine());
                    }
                }
            } catch (IOException ex) {
                throw new ServerException(ex);
            }

            final Gson gson = GsonPool.getInstance().borrowGson();
            try {
                updLog = gson.fromJson(recData.toString(), Updater.class);
            } catch (JsonSyntaxException ex) {
                throw new ServerException("Не возможно интерпритировать сохраненные данные.\n" + ex.toString());
            } finally {
                GsonPool.getInstance().returnGson(gson);
                recData.setLength(0);
            }
        } else {
            updLog = new Updater();
        }
        return updLog;
    }

    private Update getCurrentUpdate() {
        Update update;
        if (updates.isEmpty()) {
            update = new Update();
            updates.addFirst(update);
        } else {
            update = updates.getFirst();
            if (update.unzippedTime != null) {
                update = new Update();
                update.lastModified = updates.getFirst().lastModified;
                update.size = updates.getFirst().size;
                update.md5 = updates.getFirst().md5;
                updates.addFirst(update);
            }
        }
        return update;
    }

    /**
     * Скачаем.
     *
     * @param url с этого урла.
     */
    public void download(String url) {
        final Update update = getCurrentUpdate();
        final Pair<Long, Long> ufile;
        try {
            ufile = Uses.aboutFile(url);
            update.url = url;
            update.checkTime = new Date();
            update.status = "Check about file";
        } catch (IOException ex) {
            log.warn("Impossimbe to check properties of update({}) file {}", update.status, url);
            update.status = "Fail check about file";
            save();
            return;
        }

        if (ufile.getKey().equals(update.lastModified) && ufile.getValue().equals(update.size)) {
            update.status = "Checked. Where is no update.";
            save();
            return;
        }

        final String theFile = Uses.TEMP_FOLDER + File.separator + url.substring(url.lastIndexOf('/') + 1);
        boolean simpleFail = false;
        try {
            Uses.downloadFile(url, theFile);
        } catch (Exception ex) {
            log.warn("Simple downloading was failed. File {}", url);
            update.status = "Simple downloading was failed";
            save();
            simpleFail = true;
        }
        if (simpleFail) {
            boolean downloadFile;
            try {
                downloadFile = repeatableDownloadFile(url, theFile);
            } catch (Exception ex) {
                downloadFile = false;
            }
            if (!downloadFile) {
                log.warn("Repeatable downloading was failed. File {}", url);
                update.status = "Repeatable downloading was failed";
                save();
                return;
            }
        }
        update.downloadedTime = new Date();
        update.url = url;
        update.file = theFile;
        update.status = "Downloaded to " + theFile;
        update.lastModified = ufile.getKey();
        update.size = ufile.getValue();
        save();
        log.info("File from {} was successfully downloaded to {}.", url, theFile);
    }

    /**
     * Распакуем.
     */
    public void unzip(String folder) {
        final Update update = getCurrentUpdate();
        if (update.unzippedTime == null && update.url != null && update.file != null && update.downloadedTime != null
                && Files.exists(FileSystems.getDefault().getPath(update.file), LinkOption.NOFOLLOW_LINKS)) { // NOSONAR

            final String md5;
            try {
                md5 = Uses.md5(update.file);
            } catch (IOException ex) {
                log.warn("Error to get MD5 for {}; update time = {}", update.file, update.checkTime);
                return;
            }
            if (md5.equals(update.md5)) {
                return;
            }

            try {
                Uses.unzip(update.file, Paths.get(folder).toAbsolutePath().normalize().toString());
            } catch (ZipException ex) {
                log.warn("Error unzip {} to {}", update.file, Paths.get(".").toAbsolutePath().normalize());
                return;
            }
            update.unzippedTime = new Date();
            update.md5 = md5;
            update.status = "Finished successfully";
            save();
            log.info("File {} was successfully unzipped to {}.", update.file, folder);
        }
    }

    private boolean repeatableDownloadFile(String urlStr, String destinationFilePath) throws InterruptedException, IOException {
        final String[] lastModified = new String[1];
        boolean needRepeat;
        long start = System.currentTimeMillis();
        int failCounter = 0;
        do {
            try {
                log.trace("Попытка скачать файл по URL= \"{}\".", urlStr);
                needRepeat = !tryDownloadFile(urlStr, destinationFilePath, lastModified);
            } catch (QException ex) {
                failCounter++;
                log.error("Ошибка при попытки скачать файл по URL= \"" + urlStr + "\". Попыток " + failCounter + ". Будет повтор попытки загрузки.", ex);
                needRepeat = true;
            } catch (Exception ex) {
                throw new IOException("Фатальная ошибка при попытки скачать файл по URL= \"" + urlStr + "\" в \"" + destinationFilePath + "\"", ex);
            }
            // при количестве ошибок больше семи...
            if (needRepeat && // надо повторить докачку и...
                    (failCounter >= 7 || //  количество ошибок уже привысило допустимую норму
                            failCounter * 3 >= 120)) { // или интервал попыток * "количество ошибок" вышло за две минуты. См. Пауза после возникновения ошибки.
                // 7 ошибок в течении двух минут...
                if (System.currentTimeMillis() - start > 2 * 60 * 1000) {
                    // 7 ошибок в течении более двух минут, нормально, продолжаем качать.
                    start = System.currentTimeMillis();
                    failCounter = 0;
                } else {
                    // 8 ошибок в две минуты, это перебор, заваливаемся.
                    log.error("Неуспешных попыток {}. Фатальная ошибка загрузки по URL= \"{}\" в \"{}\". Загрузка прекращается.", failCounter, urlStr,
                            destinationFilePath);
                    throw new IOException("Достигнут предел неуспешных попыток " + failCounter + " скачать файл по URL= \"" + urlStr + "\" в \"" + destinationFilePath + "\"");
                }
            }
            if (needRepeat) {
                Thread.sleep(3 * 1000L); // Пауза после возникновения ошибки.
            }
        } while (needRepeat);
        return true;
    }

    /**
     * Попытаться загрузить файл.
     *
     * @param urlStr              URL источника
     * @param destinationFilePath полный путь файла назначения.
     * @param brokenLastModified  массив с единственным элементом. В этом элементе строка времени изменения файла, пришедшее в заголовках при закачке по HTTP.
     * @return true если все скачается нормально. Если что-то пошло не так, то вылетит исключение.
     * @throws QException  Если что-то произошло в момент загрузки. Разрыв какой-нибудь. Теоритически можно продолжить докачку с последнего места.
     * @throws IOException исключение при подготовки загрузки. Это локальная проблема, до закачки еще не дошло. нечего продолжать пока локально разваливается.
     */
    private boolean tryDownloadFile(String urlStr, String destinationFilePath, String[] brokenLastModified) throws QException, IOException {
        final File file;
        final URL url;
        // Подготовка. Создадим все ресурсы, необходимые для загрузки.
        try {
            file = new File(destinationFilePath);
            url = new URL(urlStr);
        } catch (Exception ex) {
            // Заваливаемся, т.к. эту ошибку повторными стартами загрузки не решить.
            throw new ServerException("Фатальная ошибка подготовки загрузки.", ex);
        }

        final HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setRequestMethod("GET");
        httpUrlConnection.addRequestProperty("Connection", "close");
        if (brokenLastModified[0] != null && file.exists() && file.length() > 0) {
            // Resume download.
            httpUrlConnection.setRequestProperty("If-Range", brokenLastModified[0]);
            httpUrlConnection.setRequestProperty("Range", "bytes=" + file.length() + "-");
        }
        httpUrlConnection.connect();
        if (httpUrlConnection.getResponseCode() == 200) {
            try (final FileOutputStream fos = new FileOutputStream(file, brokenLastModified[0] != null);
                 final BufferedOutputStream bout = new BufferedOutputStream(fos, 1024)) {
                log.debug("Старт чтения по URL= \"{}\" в файл {}, время модификации {}", urlStr, destinationFilePath, brokenLastModified[0]);
                try {
                    // Initial download.
                    brokenLastModified[0] = httpUrlConnection.getHeaderField("Last-Modified");
                    transfuseData(bout, httpUrlConnection);
                    log.trace("Загрузка завершена успешно по URL= \"{}\" в файл {}", urlStr, destinationFilePath);
                } catch (Exception ex) {
                    // Не заваливаемся, т.к. эту ошибку пытаемся "решить" повторными стартами загрузки.
                    throw new QException("Ошибка загрузки. Должна быть перехвачена и загрузка должна пытаться быть продолджена.", ex);
                }
            }
        } else {
            log.error("Код ответа {}. Ошибка загрузки из {}. Загрузка прекращена.", httpUrlConnection.getResponseCode(), urlStr);
            return true; // типа все, больше никиках попыток.
        }
        return true;
    }

    private void transfuseData(BufferedOutputStream bout, HttpURLConnection httpUrlConnection) throws IOException, InterruptedException {
        try (final BufferedInputStream in = new BufferedInputStream(httpUrlConnection.getInputStream())) {
            // качаем байтика по килобайтику.
            byte[] data = new byte[1024];
            int read;
            int cnt = 0;
            long time = System.currentTimeMillis();
            while ((read = in.read(data, 0, 1024)) >= 0) {
                bout.write(data, 0, read);

                // Ограничение по трафику
                cnt++;
                if (cnt >= 1000000) {  // Вот такое условное ограничение. В килобайтах в секунду.
                    cnt = 0;
                    final long delta = System.currentTimeMillis() - time;
                    if (delta < 1001) {
                        Thread.sleep(1000 - delta);
                    }
                    time = System.currentTimeMillis();
                }
            }
        }
    }
}
