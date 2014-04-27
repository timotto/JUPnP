package com.ubergrund.upnppetze;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.renderingcontrol.lastchange.EventedValueChannelMute;
import org.fourthline.cling.support.renderingcontrol.lastchange.EventedValueChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tim on 26/04/14.
 */
public class Petze implements Runnable, Scherge {

    private final Thread clientThread;
    private final Spitzel listener;
    private final Kartei kartei;

    private UpnpService upnpService;

    public Petze() {
        clientThread = new Thread(this);
        clientThread.setDaemon(false);
        listener = new Spitzel(this);
        kartei = new Kartei();
    }

    protected void onPetzen(RemoteDevice device, RemoteService service, EventedValue eventedValue) {
    }

    public void start() {
        clientThread.start();
    }

    public void stop() {
//        if (!clientThread.isAlive())
//            return;
        if (upnpService != null)
            upnpService.shutdown();

        try {
            clientThread.join(0);
        } catch (InterruptedException ignored) {
        }
    }

    public void search() {
        // Send a search message to all devices and services, they should respond soon
        upnpService.getControlPoint().search(new STAllHeader());
    }

    @Override
    public void onAvTransportAdded(RemoteDevice remoteDevice, RemoteService service) {
        upnpService.getControlPoint().execute(new Ermittler(service, new AVTransportLastChangeParser()) {
            @Override
            protected void onLastChange(LastChange lastChange) {
                super.onLastChange(lastChange);


            }
        });
    }

    @Override
    public void onRenderingControlAdded(final RemoteDevice remoteDevice, final RemoteService remoteService) {

        upnpService.getControlPoint().execute(new Ermittler(remoteService, new RenderingControlLastChangeParser()){

            @Override
            protected void eventReceived(GENASubscription subscription) {
//                final String xml = subscription.getCurrentValues().get("LastChange").toString().replaceAll("metadata-1-0/AVT_RCS", "metadata-1-0/RCS");
                final String xml = subscription.getCurrentValues().get("LastChange").toString();
                try {
                    LastChange lastChange = new LastChange(
                            lastChangeParser,
                            xml
                    );
                    System.out.println("[Ermittler] parser success: ");
                    System.out.println("\tdata = "+ xml);
                    onLastChange(lastChange);
                } catch (Exception e) {
                    System.err.println("[Ermittler] parser failed: " + e.getMessage());
                    System.err.println("\tlastChangeParser = " + lastChangeParser);
                    System.err.println("\tdata = "+ xml);
                }
            }

            @Override
            protected void onLastChange(LastChange lastChange) {
                super.onLastChange(lastChange);
                final EventedValueChannelVolume volume = lastChange.getEventedValue(0, EventedValueChannelVolume.class);
                final EventedValueChannelMute mute = lastChange.getEventedValue(0, EventedValueChannelMute.class);

                if (volume != null)
                    kartei.get(remoteDevice).setMasterVolume(volume.getValue().getVolume());

                if (mute != null)
                    kartei.get(remoteDevice).setMasterMute(mute.getValue().getMute());
            }
        });
    }

    public void run() {
        // This will create necessary network resources for UPnP right away
        System.out.println("Starting Cling...");
        upnpService = new UpnpServiceImpl(listener);

        // Send a search message to all devices and services, they should respond soon
        upnpService.getControlPoint().search(new STAllHeader());
    }

    public static void main(String[] args) throws Exception {

        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        final PrintWriter out = new PrintWriter(System.out);

        final Petze petze = new Petze();
        petze.start();
        try {
            String line = "";
            do {
                if ("quit".equals(line))
                    break;
                else if ("search".equals(line))
                    petze.search();
                else if (line.startsWith("list")) {
                    final List<RemoteDevice> list;
                    if (line.startsWith("list av")) {
                        list = petze.getListener().getAvTransportDevices();
                    } else if (line.startsWith("list rc")) {
                        list = petze.getListener().getRenderingControlDevices();
                    } else {
                        list = null;
                    }
                    if (list != null) {
                        for(RemoteDevice device : list) {
                            System.out.println(device.toString());
                            final DeviceDetails details = device.getDetails();
                            System.out.println("\t"+details.getFriendlyName());
                            System.out.println("\t"+details.getModelDetails().getModelName());
                        }
                    }
                }

                out.print("> ");
                out.flush();
            } while ((line = in.readLine()) != null);
        } finally {
            petze.stop();
        }

    }

    // debug?

    public Spitzel getListener() {
        return listener;
    }

}
