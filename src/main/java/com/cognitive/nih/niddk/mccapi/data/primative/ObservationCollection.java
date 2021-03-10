package com.cognitive.nih.niddk.mccapi.data.primative;

import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashMap;


public class ObservationCollection {

    @JsonIgnore
    private HashMap<String, ObservationList> observationMap = new HashMap<>();

    public void add(MccObservation obs, String defSystem)
    {
        String key = obs.getCode().getKey(defSystem);
        ObservationList fndList;
        if (!observationMap.containsKey(key))
        {
            fndList = new ObservationList();
            observationMap.put(key,fndList);

            MccCoding cd = obs.getCode().getCode(defSystem);
            fndList.setPrimaryCode(cd);
        }
        else
        {
            fndList = observationMap.get(key);
        }
        fndList.add(obs);
    }

    public ObservationList[] getObservations() {
        return observationMap.values().toArray(new ObservationList[0]);
    }

    public void sort(Comparator<MccObservation> comparator)
    {
        for (ObservationList list: observationMap.values())
        {
            list.sort(comparator);
        }
    }
}

