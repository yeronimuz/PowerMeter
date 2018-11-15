package com.lankheet.localstorage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.pmagent.p1.P1Datagram;
import com.lankheet.utils.JsonUtil;

/**
 * Store the measurement in a file in JSON format
 * 
 * @author jeroen
 *
 */
public class LocalStorageFile implements LocalStorage {
	private static final Logger LOG = LoggerFactory.getLogger(LocalStorageFile.class);

	private Path path;

	@Override
	public void activate(String path) {
		this.path = Paths.get(path);
		try {
			Files.createDirectories(this.path);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	@Override
	public void deactivate() {
	}

	/**
	 * Store the P1Datagram on disk
	 * Use the timestamp from P1 as filename
	 */
	@Override
	public void storeP1Measurement(P1Datagram datagram) {
		String fileName = datagram.getKey() + ".json";
		File file = new File(path.toFile(), fileName);
		try {
			boolean bOk = file.createNewFile();
			if (!bOk) {
				throw new IOException("File " + file.getName() + " already exists");
			}
			LOG.debug("File " + datagram.getKey() + ".json created");
		} catch (IOException e1) {
			LOG.error(e1.getMessage());
		}
		FileWriter fw;
		try {
			fw = new FileWriter(file);
			fw.write(JsonUtil.toJson(datagram));
			fw.flush();
			fw.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}


	/** 
	 * Get the file-based stored datagrams 
	 * This method returns all files in a given directory
	 * @param path The directory where to look for the datagram files
	 * @return Array with File object for each found file
	 */
	public File[] getStoredMeasurements(Path path) {
		File[] files = path.toFile().listFiles();
		return files;
	}
}
