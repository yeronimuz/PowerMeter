package com.lankheet.pmagent.beans;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lankheet.pmagent.p1.MeasurementType;

/**
 * A Measurement is a single information item (from a P1 datagram)
 */
public class Measurement {
	@JsonProperty
	private LocalDateTime timeStamp;

	@JsonProperty
	private MeasurementType type;

	@JsonProperty
	private double value;

	public Measurement() {}
	@JsonCreator
	public Measurement(@JsonProperty("timeStamp") LocalDateTime timeStamp, @JsonProperty("type") MeasurementType type,
			@JsonProperty("value") double value) {
		this.type = type;
		this.timeStamp = timeStamp;
		this.value = value;
	}

	public MeasurementType getType() {
		return type;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Measurement [timeStamp=" + timeStamp + ", type=" + type + ", value=" + value + "]";
	}
}
