package ru.apertum.qsystem.common;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import ru.apertum.qsystem.SuiteListenerForDb;
import ru.apertum.qsystem.client.QProperties;
import ru.apertum.qsystem.common.cmd.RpcGetAllServices;
import ru.apertum.qsystem.common.cmd.RpcGetGridOfDay;
import ru.apertum.qsystem.common.cmd.RpcGetGridOfWeek;
import ru.apertum.qsystem.common.exceptions.QException;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.server.Exit;
import ru.apertum.qsystem.server.QServer;
import ru.apertum.qsystem.server.ServerProps;
import ru.apertum.qsystem.server.model.QAuthorizationCustomer;
import ru.apertum.qsystem.server.model.QProperty;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QStandards;
import ru.apertum.qsystem.server.model.infosystem.QInfoItem;
import ru.apertum.qsystem.server.model.response.QRespItem;
import ru.apertum.qsystem.server.model.results.QResult;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Listeners({SuiteListenerForDb.class})
@Test(groups = "NetCommander2", singleThreaded = true, dependsOnGroups = "NetCommander")
public class NetCommanderTest2 {

    private int port = 5589;
    private INetProperty netProperty;

    private QService service;

    private QService srv2usr1;
    private QService srv1usr1;

    @BeforeTest
    public void init() throws QException {
        Thread thread = new Thread(() -> QServer.makeSocketAndRun(port));
        thread.setDaemon(true);
        thread.start();


        setUp();
        RpcGetAllServices.ServicesForWelcome services = NetCommander.getServices(netProperty);
        assertNotNull(services);
        assertEquals(services.getRoot().getName(), "Root of services");
        assertTrue(services.getRoot().getChildren().size() > 0);
        QServiceTree.sailToStorm(services.getRoot(), srv -> {
            QService qService = (QService) srv;
            if (qService.getId().equals(2L)) {
                service = qService;
            }
            if (qService.getId().equals(1548933145503L)) {
                srv1usr1 = qService;
            }
            if (qService.getId().equals(1548941906510L)) {
                srv2usr1 = qService;
            }
        });
        assertNotNull(service);
        assertNotNull(srv1usr1);
        assertNotNull(srv2usr1);

    }

    @AfterTest
    public void done() {
        try {
            Exit.sendExit("exit", "127.0.0.1", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @BeforeMethod()
    public void setUp() {
        netProperty = new INetProperty() {
            @Override
            public Integer getPort() {
                return port;
            }

            @Override
            public InetAddress getAddress() {
                try {
                    return InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @AfterMethod
    public void next() {
    }

    @Test(groups = "NetCommander2")
    public void testAboutService() throws QException, DocumentException {
        Element config = NetCommander.getBoardConfig(netProperty);
        assertNotNull(config);
        assertEquals(config.getName(), "board");
    }

    @Test(groups = "NetCommander2")
    public void testPreGridOfDay() throws QException {
        assertNotNull(service);
        RpcGetGridOfDay.GridDayAndParams preGridOfDay = NetCommander.getPreGridOfDay(netProperty, service.getId(), new Date(), -1);
        assertNotNull(preGridOfDay);
        assertEquals(preGridOfDay.getAdvanceLimit(), 1);
    }

    @Test(groups = "NetCommander2")
    public void testGridOfWeek() throws QException {
        assertNotNull(service);
        RpcGetGridOfWeek.GridAndParams gridOfWeek = NetCommander.getGridOfWeek(netProperty, service.getId(), new Date(), -1);
        assertNotNull(gridOfWeek);
        assertEquals(gridOfWeek.getAdvanceLimit(), 1);
    }

    @Test(groups = "NetCommander2")
    public void testResporseList() throws QException {
        QRespItem resporseList = NetCommander.getResporseList(netProperty);
        assertNotNull(resporseList);
        assertEquals(resporseList.getName(), "Responses");
    }

    @Test(groups = "NetCommander2")
    public void testInfoTree() throws QException {
        QInfoItem infoTree = NetCommander.getInfoTree(netProperty);
        assertNotNull(infoTree);
        assertEquals(infoTree.getName(), "Infosystem");
    }

    @Test(groups = "NetCommander2")
    public void testClientAuthorization() throws QException {
        QAuthorizationCustomer client = NetCommander.getClientAuthorization(netProperty, "noId");
        assertNull(client);
        client = NetCommander.getClientAuthorization(netProperty, "1");
        assertNotNull(client);
        assertEquals(client.getAuthId(), "1");
        assertEquals(client.getName(), "Jone");
    }

    @Test(groups = "NetCommander2")
    public void testResultsList() throws QException {
        LinkedList<QResult> resultsList = NetCommander.getResultsList(netProperty);
        assertNotNull(resultsList);
        assertEquals(resultsList.size(), 3);
    }

    @Test(groups = "NetCommander2")
    public void testStandard() throws QException {
        QStandards standards = NetCommander.getStandards(netProperty);
        assertNotNull(standards);
        assertEquals(standards.getDowntimeMax().intValue(), 10);
    }

    @Test(groups = "NetCommander2")
    public void testProperties() throws Exception {
        Field privateModuleField = QConfig.class.getDeclaredField("qModule");
        privateModuleField.setAccessible(true);
        privateModuleField.set(QConfig.cfg(), QModule.server);
        try {
            QProperties properties = QProperties.get();
            properties.load(netProperty, true);// <<===============

            assertNotNull(properties.getSection("TestSec"));
            assertNull(properties.getSection("bad Name"));
            assertEquals(properties.getSection("TestSec").getProperties().size(), 1);
            QProperty property = properties.getSection("TestSec").getProperty("test_key");
            assertEquals(property.getValue(), "1.23");
            property.setValue("New value 1");


            // II
            ArrayList<QProperty> newProps = new ArrayList<>();
            newProps.add(property);
            newProps.add(new QProperty("NewSec1", "newKey1", "value1"));
            newProps.add(new QProperty("NewSec1", "newKey2", "value2"));
            Map<String, ServerProps.Section> saved = properties.save(netProperty, newProps);// <<===============
            assertNotNull(saved);
            assertEquals(saved.size(), 2);
            assertEquals(saved.get("TestSec").getProperty("test_key").getId(), property.getId());
            assertEquals(saved.get("TestSec").getProperty("test_key").getValue(), "New value 1");
            assertEquals(saved.get("NewSec1").getProperty("newKey1").getValue(), "value1");
            assertEquals(saved.get("NewSec1").getProperty("newKey2").getValue(), "value2");


            // III
            saved = QProperties.get().reload();
            assertNotNull(saved);
            assertEquals(saved.size(), 2);
            assertEquals(saved.get("TestSec").getProperty("test_key").getId(), property.getId());
            assertEquals(saved.get("TestSec").getProperty("test_key").getValue(), "New value 1");
            assertEquals(saved.get("NewSec1").getProperty("newKey1").getValue(), "value1");
            assertEquals(saved.get("NewSec1").getProperty("newKey2").getValue(), "value2");
        } finally {
            privateModuleField.set(QConfig.cfg(), QModule.unknown);
        }
    }

    @Test(groups = "NetCommander2")
    public void testBanedList() throws QException {
        LinkedList<String> banedList = NetCommander.getBanedList(netProperty);
        assertNotNull(banedList);
        assertEquals(banedList.size(), 0);
    }

    @Test(groups = "NetCommander2")
    public void testInitRoll() {
        try {
            NetCommander.initRoll(netProperty, 1L, 1, 100, 2);
        } catch (Exception e) {
            assertEquals(e.hashCode(), 1);
        }
    }

}