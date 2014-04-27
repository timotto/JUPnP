package com.ubergrund.upnppetze;

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.lastchange.Event;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.lastchange.LastChangeParser;

import java.util.Date;
import java.util.Map;

/**
 * Created by tim on 26/04/14.
 */
public class Ermittler<T> extends SubscriptionCallback {

    protected final LastChangeParser lastChangeParser;

    protected Ermittler(Service service, LastChangeParser lastChangeParser) {
        super(service);
        this.lastChangeParser = lastChangeParser;
    }

    @Override
    protected void failed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception, String defaultMsg) {
        System.out.println("[Ermittler] failed: subscription=" + subscription + ", exception=" +exception +", responseStatu="+responseStatus + ", defaultMsg="+defaultMsg);
    }

    @Override
    protected void established(GENASubscription subscription) {
//        System.out.println("[Ermittler] established: subscription="+subscription);
    }

    @Override
    protected void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {
//        System.out.println("[Ermittler] ended. subscription=" + subscription + ", reason=" +reason +", responseStatu="+responseStatus );
        System.out.println("[Ermittler] ended. subscription=" + subscription + ", reason=" +reason +", responseStatu="+responseStatus );
    }

    @Override
    protected void eventReceived(GENASubscription subscription) {

        try {
            LastChange lastChange = new LastChange(
                    lastChangeParser,
                    subscription.getCurrentValues().get("LastChange").toString()
            );
            onLastChange(lastChange);
        } catch (Exception e) {
            System.err.println("[Ermittler] parser failed: " + e.getMessage());
            System.err.println("\tlastChangeParser = " + lastChangeParser);
            System.err.println("\tdata = "+subscription.getCurrentValues().get("LastChange").toString());
        }
    }

    @Override
    protected void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
        System.out.println("[Ermittler] eventsMissed: subscription="+subscription+", numberOfMissedEvents="+numberOfMissedEvents);
    }

    protected void onLastChange(LastChange lastChange) {
        System.out.println("[Ermittler] onLastChange() " + new Date());
        System.out.println("\tlastChange = " + lastChange);
    }
}
