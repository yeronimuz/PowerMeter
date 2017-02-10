package com.lankheet.localstorage;

import com.lankheet.pmagent.p1.P1Datagram;

public interface LocalStorage {
	
	void activate(String path);
	
	void deactivate();
	
	void storeP1Measurement(P1Datagram datagram);

}
