/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import org.hl7.fhir.r4.model.Coding;

public interface IFHIRNormalizer {
    public boolean requiresNormalization(Coding cd);
    public Coding copyCodingNormalized(Coding cd);
    public void normalizeCoding(Coding cd);
}
