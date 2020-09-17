package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data class MccDosage {
    private int sequence;
    private String text;
    private String patientInstructions;
    private MccCodeableConcept[] additionInstructions;
    private MccTiming timing;
    private boolean asNeededBoolean;
    private MccCodeableConcept asNeededCodableConcept;
    private MccCodeableConcept site;
    private MccCodeableConcept route;
    private MccCodeableConcept method;
    private DoseAndRate[] doseAndRate;
    private MccRatio maxDosePerPeriod;
    private MccSimpleQuantity maxDosePerAdministration;
    private MccSimpleQuantity maxDosePerLifetime;

    public DoseAndRate[] createDoseAndRateArray(int size)
    {
        doseAndRate = new DoseAndRate[size];
        for (int i = 0; i< size; i++)
        {
            doseAndRate[i] = new DoseAndRate();
        }
        return doseAndRate;
    }

    @JsonInclude(JsonInclude.Include. NON_NULL)
    public @Data class DoseAndRate
    {
        private MccCodeableConcept type;
        private MccRange doseRange;
        private MccQuantity doseQuantity;
        private MccRatio rateRatio;
        private MccRange rateRange;
        private MccSimpleQuantity rateQuantity;

    }


}
