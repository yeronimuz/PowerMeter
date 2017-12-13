/**
 * MIT License
 * 
 * Copyright (c) 2017 Lankheet Software and System Solutions
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
