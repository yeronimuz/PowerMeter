package com.lankheet.pmagent;

import java.util.Date;

import org.joda.time.DateTime;

public class P1Datagram {
	private byte VersionInfo;
	private DateTime dateTimeStamp;
	private String equipmentId01;
	private String equipmentId00;
	private double consumedPowerTariff1; 
	private double producedPowerTariff1;
	private double consumedPowerTariff2;
	private double producedPowerTariff2;
	private byte currentTariff;           // 2
	private double currentConsumedPwr;        // kW
	private double currentDeliveredPwr;         // kW
	/** nr.of. power failures in any phase */
	private int nrOfPowerFailuresInAnyPhase;       
	private int nrOfLongPowerFailuresInAnyPhase;           // #
	private String powerFailureEventLog;  // 
	private byte nrOfVoltageSagsInPhaseL1;          // 
	private byte nrOfVoltageSagsInPhaseL2;          // nr.of voltage sags in phase L2
	private short textMessageCodes;               // numeric 8 digits
	private String textMessage;               // max 1024 chars
	private double instantaneousCurrentL1;          // 
	private double instantaneousActivePowerL1PlusP;       // (kW)
	private double instantaneousActivePowerL1MinP;        // Instantaneous active power L1 -P
	private byte deviceType;             // Device Type (003)
	private double consumedGas; // (151009120000S)(00086.298*m3) // consumed gas
	
	/**
	 * @return the versionInfo
	 */
	public byte getVersionInfo() {
		return VersionInfo;
	}
	/**
	 * @param versionInfo the versionInfo to set
	 */
	public void setVersionInfo(byte versionInfo) {
		VersionInfo = versionInfo;
	}
	/**
	 * @return the dateTimeStamp
	 */
	public DateTime getDateTimeStamp() {
		return dateTimeStamp;
	}
	/**
	 * @param dateTimeStamp the dateTimeStamp to set
	 */
	public void setDateTimeStamp(DateTime dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}
	/**
	 * @return the equipmentId01
	 */
	public String getEquipmentId01() {
		return equipmentId01;
	}
	/**
	 * @param equipmentId01 the equipmentId01 to set
	 */
	public void setEquipmentId01(String equipmentId01) {
		this.equipmentId01 = equipmentId01;
	}
	/**
	 * @return the equipmentId00
	 */
	public String getEquipmentId00() {
		return equipmentId00;
	}
	/**
	 * @param equipmentId00 the equipmentId00 to set
	 */
	public void setEquipmentId00(String equipmentId00) {
		this.equipmentId00 = equipmentId00;
	}
	/**
	 * @return the consumedPowerTariff1
	 */
	public double getConsumedPowerTariff1() {
		return consumedPowerTariff1;
	}
	/**
	 * @param consumedPowerTariff1 the consumedPowerTariff1 to set
	 */
	public void setConsumedPowerTariff1(double consumedPowerTariff1) {
		this.consumedPowerTariff1 = consumedPowerTariff1;
	}
	/**
	 * @return the deliveredPowerTariff1
	 */
	public double getDeliveredPowerTariff1() {
		return producedPowerTariff1;
	}
	/**
	 * @param deliveredPowerTariff1 the deliveredPowerTariff1 to set
	 */
	public void setProducedPowerTariff1(double deliveredPowerTariff1) {
		this.producedPowerTariff1 = deliveredPowerTariff1;
	}
	/**
	 * @return the consumedPowerTariff2
	 */
	public double getConsumedPowerTariff2() {
		return consumedPowerTariff2;
	}
	/**
	 * @param consumedPowerTariff2 the consumedPowerTariff2 to set
	 */
	public void setConsumedPowerTariff2(double consumedPowerTariff2) {
		this.consumedPowerTariff2 = consumedPowerTariff2;
	}
	/**
	 * @return the producedPowerTariff2
	 */
	public double getProducedPowerTariff2() {
		return producedPowerTariff2;
	}
	/**
	 * @param producedPowerTariff2 the producedPowerTariff2 to set
	 */
	public void setProducedPowerTariff2(double producedPowerTariff2) {
		this.producedPowerTariff2 = producedPowerTariff2;
	}
	/**
	 * @return the currentTariff
	 */
	public byte getCurrentTariff() {
		return currentTariff;
	}
	/**
	 * @param currentTariff the currentTariff to set
	 */
	public void setCurrentTariff(byte currentTariff) {
		this.currentTariff = currentTariff;
	}
	/**
	 * @return the currentConsumedPwr
	 */
	public double getCurrentConsumedPwr() {
		return currentConsumedPwr;
	}
	/**
	 * @param currentConsumedPwr the currentConsumedPwr to set
	 */
	public void setCurrentConsumedPwr(double currentConsumedPwr) {
		this.currentConsumedPwr = currentConsumedPwr;
	}
	/**
	 * @return the currentDeliveredPwr
	 */
	public double getCurrentDeliveredPwr() {
		return currentDeliveredPwr;
	}
	/**
	 * @param currentDeliveredPwr the currentDeliveredPwr to set
	 */
	public void setCurrentDeliveredPwr(double currentDeliveredPwr) {
		this.currentDeliveredPwr = currentDeliveredPwr;
	}
	/**
	 * @return the nrOfPowerFailuresInAnyPhase
	 */
	public int getNrOfPowerFailuresInAnyPhase() {
		return nrOfPowerFailuresInAnyPhase;
	}
	/**
	 * @param nrOfPowerFailuresInAnyPhase the nrOfPowerFailuresInAnyPhase to set
	 */
	public void setNrOfPowerFailuresInAnyPhase(int nrOfPowerFailuresInAnyPhase) {
		this.nrOfPowerFailuresInAnyPhase = nrOfPowerFailuresInAnyPhase;
	}
	/**
	 * @return the nrOfLongPowerFailuresInAnyPhase
	 */
	public int getNrOfLongPowerFailuresInAnyPhase() {
		return nrOfLongPowerFailuresInAnyPhase;
	}
	/**
	 * @param nrOfLongPowerFailuresInAnyPhase the nrOfLongPowerFailuresInAnyPhase to set
	 */
	public void setNrOfLongPowerFailuresInAnyPhase(int nrOfLongPowerFailuresInAnyPhase) {
		this.nrOfLongPowerFailuresInAnyPhase = nrOfLongPowerFailuresInAnyPhase;
	}
	/**
	 * @return the powerFailureEventLog
	 */
	public String getPowerFailureEventLog() {
		return powerFailureEventLog;
	}
	/**
	 * @param powerFailureEventLog the powerFailureEventLog to set
	 */
	public void setPowerFailureEventLog(String powerFailureEventLog) {
		this.powerFailureEventLog = powerFailureEventLog;
	}
	/**
	 * @return the nrOfVoltageSagsInPhaseL1
	 */
	public byte getNrOfVoltageSagsInPhaseL1() {
		return nrOfVoltageSagsInPhaseL1;
	}
	/**
	 * @param nrOfVoltageSagsInPhaseL1 the nrOfVoltageSagsInPhaseL1 to set
	 */
	public void setNrOfVoltageSagsInPhaseL1(byte nrOfVoltageSagsInPhaseL1) {
		this.nrOfVoltageSagsInPhaseL1 = nrOfVoltageSagsInPhaseL1;
	}
	/**
	 * @return the nrOfVoltageSagsInPhaseL2
	 */
	public byte getNrOfVoltageSagsInPhaseL2() {
		return nrOfVoltageSagsInPhaseL2;
	}
	/**
	 * @param nrOfVoltageSagsInPhaseL2 the nrOfVoltageSagsInPhaseL2 to set
	 */
	public void setNrOfVoltageSagsInPhaseL2(byte nrOfVoltageSagsInPhaseL2) {
		this.nrOfVoltageSagsInPhaseL2 = nrOfVoltageSagsInPhaseL2;
	}
	/**
	 * @return the textMessageCodes
	 */
	public short getTextMessageCodes() {
		return textMessageCodes;
	}
	/**
	 * @param textMessageCodes the textMessageCodes to set
	 */
	public void setTextMessageCodes(short textMessageCodes) {
		this.textMessageCodes = textMessageCodes;
	}
	/**
	 * @return the textMessage
	 */
	public String getTextMessage() {
		return textMessage;
	}
	/**
	 * @param textMessage the textMessage to set
	 */
	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}
	/**
	 * @return the instantaneousCurrentL1
	 */
	public double getInstantaneousCurrentL1() {
		return instantaneousCurrentL1;
	}
	/**
	 * @param instantaneousCurrentL1 the instantaneousCurrentL1 to set
	 */
	public void setInstantaneousCurrentL1(double instantaneousCurrentL1) {
		this.instantaneousCurrentL1 = instantaneousCurrentL1;
	}
	/**
	 * @return the instantaneousActivePowerL1PlusP
	 */
	public double getInstantaneousActivePowerL1PlusP() {
		return instantaneousActivePowerL1PlusP;
	}
	/**
	 * @param instantaneousActivePowerL1PlusP the instantaneousActivePowerL1PlusP to set
	 */
	public void setInstantaneousActivePowerL1PlusP(double instantaneousActivePowerL1PlusP) {
		this.instantaneousActivePowerL1PlusP = instantaneousActivePowerL1PlusP;
	}
	/**
	 * @return the instantaneousActivePowerL1MinP
	 */
	public double getInstantaneousActivePowerL1MinP() {
		return instantaneousActivePowerL1MinP;
	}
	/**
	 * @param instantaneousActivePowerL1MinP the instantaneousActivePowerL1MinP to set
	 */
	public void setInstantaneousActivePowerL1MinP(double instantaneousActivePowerL1MinP) {
		this.instantaneousActivePowerL1MinP = instantaneousActivePowerL1MinP;
	}
	/**
	 * @return the deviceType
	 */
	public byte getDeviceType() {
		return deviceType;
	}
	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(byte deviceType) {
		this.deviceType = deviceType;
	}
	public double getConsumedGas() {
		return consumedGas;
	}
	public void setConsumedGas(double consumedGas) {
		this.consumedGas = consumedGas;
	}

}
