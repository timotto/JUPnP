package com.ubergrund.upnppetze;

import org.fourthline.cling.model.meta.RemoteDevice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tim on 27/04/14.
 */
public class Kartei {

    private final Map<RemoteDevice,JUPnPDevice> deviceMap = new HashMap<RemoteDevice, JUPnPDevice>();

    public JUPnPDevice get(RemoteDevice remoteDevice) {
        synchronized (deviceMap) {
            if (!deviceMap.containsKey(remoteDevice))
                deviceMap.put(remoteDevice, new JUPnPDevice());

            return deviceMap.get(remoteDevice);
        }
    }
}
