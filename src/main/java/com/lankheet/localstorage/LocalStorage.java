package com.lankheet.localstorage;

import com.lankheet.pmagent.P1Datagram;

public interface LocalStorage {
	
	void activate();
	
	void deactivate();
	
	void storeP1Measurement(P1Datagram datagram);

}
