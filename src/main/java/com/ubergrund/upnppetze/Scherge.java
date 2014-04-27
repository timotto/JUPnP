package com.ubergrund.upnppetze;

import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;

/**
 * Created by tim on 26/04/14.
 */
public interface Scherge {
    void onAvTransportAdded(RemoteDevice remoteDevice, RemoteService service);
    void onRenderingControlAdded(RemoteDevice remoteDevice, RemoteService service);
}
