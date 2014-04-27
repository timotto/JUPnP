package com.ubergrund.upnppetze;

/**
 * Created by tim on 27/04/14.
 */
public class JUPnPDevice {

    /**
     * STOPPED, PLAYING, TRANSITIONING, ...
     */
    private String transportState;

    private String avTransportUri;

    private String currentTrackUri;

    private int masterVolume;

    private boolean masterMute;

    protected void onChange() {

    }

    public String getTransportState() {
        return transportState;
    }

    public void setTransportState(String transportState) {
        if (transportState==null)throw new IllegalArgumentException("argument can not be null");
        if (transportState.equals(this.transportState))return;
        this.transportState = transportState;
        onChange();
    }

    public String getAvTransportUri() {
        return avTransportUri;
    }

    public void setAvTransportUri(String avTransportUri) {
        if (avTransportUri==null)throw new IllegalArgumentException("argument can not be null");
        if (avTransportUri.equals(this.avTransportUri))return;
        this.avTransportUri = avTransportUri;
    }

    public String getCurrentTrackUri() {
        return currentTrackUri;
    }

    public void setCurrentTrackUri(String currentTrackUri) {
        if (currentTrackUri==null)throw new IllegalArgumentException("argument can not be null");
        if (currentTrackUri.equals(this.currentTrackUri))return;
        this.currentTrackUri = currentTrackUri;
    }

    public int getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(int masterVolume) {
        if (masterVolume == this.masterVolume)return;
        this.masterVolume = masterVolume;
    }

    public boolean isMasterMute() {
        return masterMute;
    }

    public void setMasterMute(boolean masterMute) {
        if (masterMute == this.masterMute)return;
        this.masterMute = masterMute;
    }
}
