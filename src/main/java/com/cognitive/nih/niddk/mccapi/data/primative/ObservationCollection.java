package com.cognitive.nih.niddk.mccapi.data.primative;

import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;


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

    public String simpleCensus()
    {
        int segments = observationMap.size();
        int tot = 0;

        Iterator<ObservationList> itr = observationMap.values().iterator();
        while (itr.hasNext())
        {
            ObservationList l = itr.next();
            tot += l.size();
        }
        return segments+" segements with "+tot+" total items";
    }
}

