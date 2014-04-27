package com.ubergrund.upnppetze;

import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tim on 26/04/14.
 */
public class Spitzel implements RegistryListener {

    private static final String SERVICE_TYPE_AVTRANSPORT = "urn:schemas-upnp-org:service:AVTransport:1";
    private static final String SERVICE_TYPE_RENDERINGCONTROL = "urn:schemas-upnp-org:service:RenderingControl:1";
    private static final String SERVICE_TYPE_CONNECTIONMANAGER = "urn:schemas-upnp-org:service:ConnectionManager:1";

    private final Object monitor = new Object();
    private final List<RemoteDevice> avTransportDevices = new ArrayList<RemoteDevice>();
    private final List<RemoteDevice> renderingControlDevices = new ArrayList<RemoteDevice>();
    private final List<ServiceType> unknownServices = new ArrayList<ServiceType>();

    private final Scherge scherge;

    public Spitzel(Scherge scherge) {
        this.scherge = scherge;
    }

    public List<RemoteDevice> getAvTransportDevices() {
        synchronized (monitor) {
            return new ArrayList<RemoteDevice>(avTransportDevices);
        }
    }

    public List<RemoteDevice> getRenderingControlDevices() {
        synchronized (monitor) {
            return new ArrayList<RemoteDevice>(renderingControlDevices);
        }
    }

    public List<ServiceType> getUnknownServices() {
        return new ArrayList<ServiceType>(unknownServices);
    }

    private boolean addTo(List<RemoteDevice> list, RemoteDevice device) {
        synchronized (monitor) {
            if (list.contains(device))
                return false;
            list.add(device);
        }
        // petzen
        return true;
    }

    private boolean removeFrom(List<RemoteDevice> list, RemoteDevice device) {
        final boolean b;
        synchronized (monitor) {
            b = list.remove(device);
        }
        // petzen
        return b;
    }

    private void onRemoteDevice(Registry registry, RemoteDevice device) {

        RemoteService avTransport = null;
//        boolean avTransport = false;
        RemoteService renderingControl = null;
//        boolean renderingControl = false;
        boolean connectionManager = false;

        final RemoteService[] services = device.getServices();
        if (services != null)
            for(RemoteService service : services) {
                final String serviceType = service.getServiceType().toString();
                if (SERVICE_TYPE_AVTRANSPORT.equals(serviceType))
                    avTransport = service;
                else if (SERVICE_TYPE_RENDERINGCONTROL.equals(serviceType))
                    renderingControl = service;
                else if (SERVICE_TYPE_CONNECTIONMANAGER.equals(serviceType))
                    connectionManager = true;
                else onUnknownService(device, service);
            }

        boolean change = false;

        if (avTransport != null && addTo(avTransportDevices, device)) {
            change = true;
            scherge.onAvTransportAdded(device, avTransport);
        } else if (avTransport == null && removeFrom(avTransportDevices, device)) {
            change = true;
//            scherge.onAvTransportRemoved(device);
        }

        if (renderingControl != null && addTo(renderingControlDevices, device)) {
            change = true;
            scherge.onRenderingControlAdded(device, renderingControl);
        } else if (renderingControl == null && removeFrom(renderingControlDevices, device)) {
            change = true;
//            scherge.onRenderingControlRemoved(device);
        }

        if (change) {
//            System.out.println("something changed %-D " + device);
            System.out.println("something changed %-D ");
        }

        for(RemoteDevice embeddedDevice : device.getEmbeddedDevices()) {
            onRemoteDevice(registry, embeddedDevice);
        }
    }

    private void onUnknownService(RemoteDevice device, RemoteService service) {
        final ServiceType serviceType = service.getServiceType();
        synchronized (unknownServices) {
            if (unknownServices.contains(serviceType))
                return;
            unknownServices.add(serviceType);
        }
//        System.out.println("Unknown service type: " + serviceType.toFriendlyString());
    }

    public void remoteDeviceDiscoveryStarted(Registry registry,
                                             RemoteDevice device) {
        System.out.println(
                "Discovery started: " + device.getDisplayString()
        );
    }

    public void remoteDeviceDiscoveryFailed(Registry registry,
                                            RemoteDevice device,
                                            Exception ex) {
        System.out.println(
                "Discovery failed: " + device.getDisplayString() + " => " + ex
        );
    }

    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        System.out.println(
                "Remote device available: " + device.getDisplayString()
        );
        onRemoteDevice(registry, device);
    }

    public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
//        System.out.print('.');
//        System.out.println(
//                "Remote device updated: " + device.getDisplayString()
//        );
        onRemoteDevice(registry, device);
    }

    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        System.out.println(
                "Remote device removed: " + device.getDisplayString()
        );
        boolean a = removeFrom(avTransportDevices, device);
        boolean b = removeFrom(renderingControlDevices, device);

        if (a || b) {
            System.out.println("something changed %-O " + device);
        }
    }

    public void localDeviceAdded(Registry registry, LocalDevice device) {
        System.out.println(
                "Local device added: " + device.getDisplayString()
        );
    }

    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        System.out.println(
                "Local device removed: " + device.getDisplayString()
        );
    }

    public void beforeShutdown(Registry registry) {
        System.out.println(
                "Before shutdown, the registry has devices: "
                        + registry.getDevices().size()
        );
    }

    public void afterShutdown() {
        System.out.println("Shutdown of registry complete!");

    }
}
