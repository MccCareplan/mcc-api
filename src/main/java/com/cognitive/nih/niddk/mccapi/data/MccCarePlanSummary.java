package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccPeriod;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hl7.fhir.r4.model.CarePlan;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MccCarePlanSummary {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String FHIRId;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Set<String> profiles;
    private Date created;
    private Date lastUpdated;
    private MccPeriod period;

    class sortByLastUpdated implements Comparator<MccCarePlanSummary>
    {
        public int compare(MccCarePlanSummary o1,  MccCarePlanSummary o2)
        {
            if (o1.lastUpdated == null) return -1;
            if (o2.lastUpdated == null) return 1;
            return o1.lastUpdated.compareTo(o2.created);
        }
    }

    public class compareByLastUpdated implements Comparator<MccCarePlanSummary>
    {
        public int compare(MccCarePlanSummary o1,  MccCarePlanSummary o2)
        {
            if (o1.lastUpdated == null) return -1;
            if (o2.lastUpdated == null) return 1;
            return o1.lastUpdated.compareTo(o2.created);
        }
    }

    public class compareByCreated implements Comparator<MccCarePlanSummary>
    {
        public int compare(MccCarePlanSummary o1,  MccCarePlanSummary o2)
        {
            if (o1.created == null) return -1;
            if (o2.created == null) return 1;
            return o1.created.compareTo(o2.created);
        }
    }

    public class compareByProfiles implements Comparator<MccCarePlanSummary>
    {
        public int compare(MccCarePlanSummary o1,  MccCarePlanSummary o2)
        {
            return o1.profiles.size() - o2.profiles.size();
        }
    }


    public Comparator<MccCarePlanSummary> CompareByLastUpdate()
    {
        return new compareByLastUpdated();
    }

    public Comparator<MccCarePlanSummary> CompareByCreate()
    {
        return new compareByCreated();
    }

    public Comparator<MccCarePlanSummary> CompareByProfiles()
    {
        return new compareByProfiles();
    }
}

