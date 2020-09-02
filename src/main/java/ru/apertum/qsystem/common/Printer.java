package ru.apertum.qsystem.common;

import ru.apertum.qsystem.client.common.WelcomeParams;
import ru.apertum.qsystem.client.forms.FWelcome;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.model.QServiceTree;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class Printer {

    /**
     * Просто утилитка для отладки печати. Запусти с параметром "1" и напечатается талончик со случайным номером.
     */
    public static void main(String[] args) throws PrinterException, IOException {
        if (args.length > 0) {
            switch (args[0]) {
                case "1": {
                    final QCustomer customer = new QCustomer(new Random().ints(1, (1000 + 1)).findFirst().getAsInt());
                    customer.setService(QServiceTree.getInstance().getRoot());
                    FWelcome.printTicket(customer);
                    break;
                }
                default: {
                    printFile(args[0]);
                }
            }
        } else {
            printFile("testprintfile.txt");
        }
    }

    private static void printFile(String fileName) throws IOException, PrinterException {
        final List<String> strings = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        final Printable canvas = new Printable() {

            Graphics2D g2;

            private int write(Graphics2D g2, String text, int line, int x, double kx, double ky, int initY) {
                g2.scale(kx, ky);
                final int y = (int) Math.round((initY + line * WelcomeParams.getInstance().lineHeigth) / ky);
                g2.drawString(text, x, y);
                g2.scale(1 / kx, 1 / ky);
                return y;
            }

            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                if (pageIndex >= 1) {
                    return Printable.NO_SUCH_PAGE;
                }
                int line = 0;
                int initY = WelcomeParams.getInstance().topMargin;
                g2 = (Graphics2D) graphics;
                for (String txt : strings) {
                    System.out.println(txt);
                    write(g2, txt, ++line, 10, 1, 1, initY);
                }
                System.out.println("-------------------------------------------------------------------------------------------");
                return Printable.PAGE_EXISTS;
            }
        };
        final PrinterJob job = PrinterJob.getPrinterJob();
        if (WelcomeParams.getInstance().printService != null) {
            job.setPrintService(WelcomeParams.getInstance().printService);
        }
        job.setPrintable(canvas);
        job.print(WelcomeParams.getInstance().printAttributeSet);
    }
}
