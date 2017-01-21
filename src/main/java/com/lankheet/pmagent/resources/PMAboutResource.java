package com.lankheet.pmagent.resources;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.lankheet.localstorage.LocalStorage;

@Path("/powermeter")
@Produces(MediaType.APPLICATION_JSON)
public class PMAgentResource {
	
	private LocalStorage localStorage;
	
	public PMAgentResource(LocalStorage localStorage) {
		this.localStorage = localStorage;
	}

	/**
	 * Returns application and version info of the powermeter agent
	 * @return PMAgent version and description
	 * @throws IOException Manifest of this JAR could not be read
	 */
	@GET
	public AboutPMAgent aboutPowerMeter() throws IOException {
		return new AboutPMAgent();
	}
}