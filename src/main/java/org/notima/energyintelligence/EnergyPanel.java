package org.notima.energyintelligence;
import de.vandermeer.asciitable.AsciiTable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnergyPanel implements EMRDeviceEventListener {
    private List<EMRDevice> devices = new ArrayList<EMRDevice>();
    private Map<String, EMRData> dataMap = new HashMap<String, EMRData>();

    public void addDevice(String deviceAddress) throws URISyntaxException {
        EMRDevice device = new EMRDevice(deviceAddress);
        device.setEventListener(this);
        device.connect();
        devices.add(device);
        draw();
    }

    public void onConnected(EMRDevice device) {
        draw();
    }

    public void onUpdated(EMRDevice device) {
        draw();
    }

    public void onData(EMRDevice device, EMRData data) {
        dataMap.put(data.getTopic(), data);
        draw();
    }

    public void onDisconnected(EMRDevice device) {
        draw();
        try {
            device.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void onError(EMRDevice device, Exception e) {
        e.printStackTrace();
    }

    public void draw() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
        for(EMRDevice device : devices) {
            drawDeviceStatusTable(device);
        }
        System.out.println("");
        drawReadingsTable();
    }

    private void drawDeviceStatusTable(EMRDevice device) {
        AsciiTable asciiTable = new AsciiTable();
        System.out.printf("%s (%s)\n", device.getStatus().getDeviceId(), device.getAddress());
        asciiTable.addRule();
        asciiTable.addRow("Key", "Value");
        asciiTable.addRule();
        asciiTable.addRow("Websocket status", String.format("%s", device.isConnected() ? "Connected":"Connecting"));
        asciiTable.addRule();
        asciiTable.addRow("Uptime", device.getStatus().getUptime());
        asciiTable.addRule();
        String render = asciiTable.render();
        System.out.println(render);
    }

    private void drawReadingsTable() {
        AsciiTable asciiTable = new AsciiTable();
        System.out.println("Data");
        asciiTable.addRule();
        asciiTable.addRow("Topic", "Value", "Unit");
        asciiTable.addRule();
        for(EMRData data : dataMap.values()) {
            asciiTable.addRow(data.getTopic(), data.getValue(), data.getUnit());
            asciiTable.addRule();
        }
        String render = asciiTable.render();
        System.out.println(render);
    }
}
