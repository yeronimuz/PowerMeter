package com.lankheet.utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class JsonUtil
{
	private static final Logger logger = LogManager.getLogger(JsonUtil.class);
	
	private static final ObjectMapper mapper = new ObjectMapper()
		.registerModule(new JodaModule())
		.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	public static final String toJson(Object o)
	{
		try
		{
			return mapper.writeValueAsString(o);
		}
		catch (JsonProcessingException e)
		{
			logger.error("Json Processing was wrong: " + e, logger);
		}

		return null;
	}
	
}
