package com.lankheet.pmagent.p1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class P1Datagram {
    @JsonProperty(value = "version")
    private byte versionInfo;

    @JsonProperty(value = "ts")
    private LocalDateTime dateTimeStamp;

    @JsonIgnore
    private String equipmentId01;

    @JsonIgnore
    private String equipmentId00;

    @JsonProperty(value = "consPT1")
    private double consumedPowerTariff1;

    @JsonProperty(value = "prodPT1")
    private double producedPowerTariff1;

    @JsonProperty(value = "consPT2")
    private double consumedPowerTariff2;

    @JsonProperty(value = "prodPT2")
    private double producedPowerTariff2;

    @JsonProperty(value = "cT")
    private byte currentTariff; // 2

    @JsonProperty(value = "cconsP")
    private double actualConsumedPwr; // kW

    @JsonProperty(value = "cprodP")
    private double actualDeliveredPwr; // kW

    /**
     * nr.of. power failures in any phase
     */
    @JsonIgnore
    private int nrOfPowerFailuresInAnyPhase;
    @JsonIgnore
    private int nrOfLongPowerFailuresInAnyPhase; // #
    @JsonIgnore
    private String powerFailureEventLog; //
    @JsonIgnore
    private byte nrOfVoltageSagsInPhaseL1; //
    @JsonIgnore
    private byte nrOfVoltageSagsInPhaseL2; // nr.of voltage sags in phase L2
    @JsonIgnore
    private short textMessageCodes; // numeric 8 digits
    @JsonIgnore
    private String textMessage; // max 1024 chars
    @JsonIgnore
    private double instantaneousCurrentL1;
    @JsonIgnore
    private double instantaneousActivePowerL1PlusP; // (kW)
    @JsonIgnore
    private double instantaneousActivePowerL1MinP; // Instantaneous active power
    // L1 -P
    @JsonIgnore
    private byte deviceType; // Device Type (003)

    @JsonIgnore
    /**
     * The key is actually not conforming to separation of concerns principle It is used for local
     * storage of datagrams (YYMMddHHss)
     */
    private String key;

    @JsonProperty(value = "consG")
    private double consumedGas; // (151009120000S)(00086.298*m3) // consumed gas

    public P1Datagram() {
    }

    @JsonCreator
    public P1Datagram(
            @JsonProperty("ts") LocalDateTime dateTimeStamp,
            @JsonProperty("consPT1") double consumedPowerTariff1, @JsonProperty("prodPT1") double producedPowerTariff1,
            @JsonProperty("consPT2") double consumedPowerTariff2, @JsonProperty("prodPT2") double producedPowerTariff2,
            @JsonProperty("cT") byte currentTariff, @JsonProperty("cconsP") double actualConsumedPwr,
            @JsonProperty("cprodP") double actualDeliveredPwr, @JsonProperty("consG") double consumedGas) {
        this.consumedPowerTariff1 = consumedPowerTariff1;
        this.consumedPowerTariff2 = consumedPowerTariff2;
        this.producedPowerTariff1 = producedPowerTariff1;
        this.producedPowerTariff2 = producedPowerTariff2;
        this.currentTariff = currentTariff;
        this.actualConsumedPwr = actualConsumedPwr;
        this.actualDeliveredPwr = actualDeliveredPwr;
        this.consumedGas = consumedGas;
    }
}
