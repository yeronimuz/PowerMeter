package com.lankheet.localstorage;

import com.lankheet.pmagent.p1.P1Datagram;

public interface LocalStorage {

    /**
     * Activate the local storage
     */
    void activate(String path);

    /**
     * Close the local storage
     */
    void deactivate();

    /**
     * Persist the measurement in local storage
     */
    void storeP1Measurement(P1Datagram datagram);
}
