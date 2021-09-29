/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.xml.crypto.XMLCryptoContext;

@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data
class MccPeriod implements Comparable {

    public static final String fhirType = "Period";

    private MccDate start;
    private MccDate end;

    @Override
    public int compareTo(Object o) {
        if (o instanceof MccPeriod) {
            return compareTo((MccPeriod) o);
        }
        if (o instanceof MccDate) {
            return compareTo((MccDate) o);
        }
        if (o instanceof MccTiming) {
            return compareTo((MccTiming) o);
        }
        log.warn("Comparison between MccPeriod and "+o.getClass().getName()+" not yet supported");
        return 0;
    }

    public int compareTo(MccTiming in)
    {
        log.warn("Comparison between MccPeriod and MccTiming not yet supported");
        return 0;
    }
    public int compareTo(MccPeriod in) {
        //Check that the period is closed
        if (!this.isOpen() && !in.isOpen()) {
            int out = this.start.compareTo(in.getStart());
            if (out == 0) {
                //Ok the the starts are equal base it on the ends
                return this.end.compareTo(in.getEnd());
            }
            return out;
        }
        if (this.isOpenStart()) {
            //We have an open start
            if (!in.isOpenStart())
                return -1;
            //Now we have to look at ends
            if (!this.isOpenEnd() && in.isOpenEnd()) {
                return 1;
            }
            return -1;
        }
        //Ok if we get here we have a start one party has an open end
        if (in.isOpenStart()) {
            return 1;
        }
        //Ok now all open starts are taken care of and we know that one party has an open end
        int out = this.start.compareTo(in.getStart());
        if (out == 0) {
            //start time match so now the one with the open end goes last
            return this.isOpenEnd() ? 1 : -1;
        }
        return out;
    }

    public int compareTo(MccDate in)
    {
        //As a rule a period will only match a date it the start and end both match
        if (isOpen()) {
            if (isOpenStart()) {
                //When the start is open we consider the end
                if (isOpenEnd()) {
                    return -1;
                }
                //We should only sort if we are beyond the end date
                return end.compareTo(in);
            }
            //Ok we have an open end and not a start
            return start.compareTo(in);
        } else {
            //The period is closed - So it is equal if it is withing the range
            int chk = start.compareTo(in);
            if (chk > 0) {
                //Start beyond date
                return 1;
            }
            if (chk == 0) {
                //Inclusive
                return 0;
            }
            chk = end.compareTo(in);
            //Follows the end
            if (chk > 0) return 1;
            //Must be between the end and start inclusive
            return 0;

        }
    }
    @JsonIgnore
    public boolean isOpen() {
        return isOpenStart() || isOpenEnd();
    }

    @JsonIgnore
    public boolean isOpenEnd() {
        if (end == null)
            return true;
        if (end.getRawDate() == null)
            return true;

        return false;
    }
    @JsonIgnore
    public boolean isOpenStart() {
        if (start == null)
            return true;
        if (start.getRawDate() == null)
            return true;

        return false;
    }
}
