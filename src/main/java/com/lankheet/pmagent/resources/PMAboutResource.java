package com.lankheet.pmagent.resources;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

@Path("/about")
@Produces(MediaType.APPLICATION_JSON)
public class PMAboutResource {

	private AboutPMAgent aboutPM;

	public PMAboutResource(AboutPMAgent aboutPM) {
		this.aboutPM = aboutPM;
	}

	/**
	 * Returns application and version info of the powermeter agent
	 * 
	 * @return PMAgent version and description
	 * @throws IOException
	 *             Manifest of this JAR could not be read
	 */
	@GET
	@Timed
	public AboutPMAgent aboutPowerMeter() throws IOException {
		return aboutPM;
	}
}