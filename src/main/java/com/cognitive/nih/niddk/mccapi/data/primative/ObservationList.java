package com.cognitive.nih.niddk.mccapi.data.primative;

import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import lombok.Data;
import org.hl7.fhir.r4.model.CodeableConcept;

import java.util.Comparator;
import java.util.LinkedList;

@Data
public class ObservationList {
    private MccCoding primaryCode;

    private LinkedList<MccObservation> observations = new LinkedList<>();


    public void add(MccObservation obs)
    {
        observations.add(obs);
    }

    public void sort(Comparator<MccObservation> comparator) {
        if (observations.size()>1)
        {
           observations.sort(comparator);
        }
    }

    public int size()
    {
        return observations.size();
    }
}
