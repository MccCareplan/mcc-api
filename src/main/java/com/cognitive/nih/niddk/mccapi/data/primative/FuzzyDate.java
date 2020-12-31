package com.cognitive.nih.niddk.mccapi.data.primative;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hl7.fhir.r4.model.*;

import javax.validation.constraints.NotBlank;
import java.util.Date;
@JsonInclude(JsonInclude.Include. NON_NULL)
public class FuzzyDate implements Comparable<FuzzyDate>{

    @NotBlank
    private Type t;
    private String text;
    private Date start;
    private Date end;
    private boolean hardSort;
    @NotBlank
    private DateType type;


    public FuzzyDate(Type t) {
        this.t = t;
        this.doTypeAnalysis(Context.NULL_CONTEXT);
    }
    public FuzzyDate(Type t, Context ctx) {
        this.t = t;
        this.doTypeAnalysis(ctx);
    }

    public FuzzyDate()
    {

    }

    @Override
    public int compareTo(FuzzyDate o) {
        switch (type)
        {
            case Date:
            case Age:
            {
                switch (o.type)
                {
                    case Date:
                    case Age:
                    {
                        return start.compareTo(o.start);
                    }
                    case Period:
                    {
                        return compareDateToPeriod(o);
                    }
                    case Range:
                    case Text:
                    case Unknown:
                    case Null:
                    default:
                    {
                        return 1;
                    }
                }
            }
            case Period:
            case Range:
            {
                switch (o.type)
                {
                    case Period:
                    {
                        return comparePeriodToPeriod(o);
                    }
                    case Date:
                    case Age:
                    {
                        return comparePeriodToDate(o);
                    }
                    case Range:
                    case Text:
                    case Unknown:
                    case Null:
                    {
                        return 1;
                    }
                    default:
                    {
                        return 0;
                    }
                }
            }
            case Text:
            case Unknown:
            case Null:
            {
                switch (o.type)
                {
                    case Text:
                    case Null:
                    case Unknown:
                    {
                        return 0;
                    }
                    default: {
                        return -1;
                    }
                }
            }

        }
        return 0;
    }

    int compareDateToPeriod(FuzzyDate o)
    {
        //There is an end
        if (o.end != null)
        {
            if (o.start != null)
            {
                int r = start.compareTo(o.start);
                if (!(r>0)) {
                    return r;
                }
            }
            return start.compareTo(o.end);
        }
        if (o.start == null)
        {
            //Open Range
            return -1;
        }
        return o.start.compareTo(start);
    }

    int comparePeriodToDate(FuzzyDate o)
    {
        if (end != null)
        {
            int r = o.start.compareTo(end);
            if ( r < 0 )
            {
                if (start == null)
                {
                    return -1;
                }
                else
                {
                    return start.compareTo(o.start);
                }
            }
            else
            {
                return r;
            }
        }
        if (o.start != null)
        {
            int r = start.compareTo(o.start);
            if (r>0)
            {
                if (end == null)
                {
                    return 1;
                }
                else
                {
                    return end.compareTo(o.start);
                }
            }
            else
            {
                return r;
            }
        }
        //Open Range
        return 1;
    }

    int comparePeriodToPeriod(FuzzyDate o)
    {
        if (start!=null && o.start!=null) {
            int out = start.compareTo(o.start);
            if (out == 0) {
                //Ok the the starts are equal base it on the ends
                if (end!= null && o.end!= null)
                {
                    return end.compareTo(o.end);
                }
                return end == null? 1:-1;
            }
            return out;
        }
        if (start == null) {
            //We have an open start
            if (o.start != null)
                return -1;
            //Now we have to look at ends
            if (end!=null && o.end==null) {
                return 1;
            }
            return -1;
        }
        //The other side has an open start
        if (o.end != null)
        {
            if (end != null)
            {
                return end.compareTo(o.end);
            }
            // We have no end so make the comparison vs start;
            return start.compareTo(o.end);
        }
        return 0;
    }

    public FuzzyDate createFromType(Type t, Context ctx)
    {
        return new FuzzyDate(t,ctx);
    }

    public static DateType findDateType(Type t) {
        if (t != null) {
            Class c = t.getClass();
            if (c == DateTimeType.class) return DateType.Date;
            else if (c == Age.class) return DateType.Age;
            else if (c == Period.class) return DateType.Period;
            else if (c == Range.class) return DateType.Range;
            else if (c == Quantity.class) return DateType.Age;  //Assume a quantity is an age here
            else if (c == StringType.class) return DateType.Text;
            else return DateType.Unknown;
        }
        return DateType.Null;
    }

    public static String buildString(Type t, Context ctx) {
        //TODO: Deal with abstracting sorting for start and end
        //      By convention all uninterpretable dates (e.g. String) sort first

        String out = null;

        if (t != null) {
            Class c = t.getClass();
            if (c == DateTimeType.class) {

                DateTimeType d = new DateTimeType();
                d = t.castToDateTime(t);
                out = Helper.dateToString(d.getValue());

            } else if (c == Age.class) {
                Quantity a = new Quantity();
                a = t.castToQuantity(t);
                String disp = a.getDisplay();
                out = String.format("At age %s", disp);
            } else if (c == Period.class) {
                Period p = new Period();
                p = t.castToPeriod(t);
                Date start = p.getStart();
                Date end = p.getEnd();
                //From s, s to e, To e
                if (start == null) {
                    out = String.format("From %s", Helper.dateToString(start));
                } else if (end == null) {
                    out = String.format("From %s to %s", Helper.dateToString(start), Helper.dateToString(end));
                } else {
                    out = String.format("To %s", Helper.dateToString(end));
                }
            } else if (c == Range.class) {
                Range r = new Range();
                r = t.castToRange(t);
                Quantity start = r.getLow();
                Quantity end = r.getHigh();
                out = String.format("Between %s and %s", start.getDisplay(), end.getDisplay());
            } else if (c == StringType.class) {
                StringType s = new StringType();
                s = t.castToString(t);
                out = s.getValue();
            } else {
                out = "a " + t.getClass().getName();
            }
        }
        return out;
    }

    private void doTypeAnalysis(Context ctx) {
        type = findDateType(t);
        switch (type) {
            case Date: {
                handleAsDate();
                break;
            }
            case Age: {
                handleAsAge(ctx);
                break;

            }
            case Period: {
                handleAsPeriod();
                break;

            }
            case Range: {
                handleAsRange();
                break;

            }
            case Text: {
                handleAsText();
                break;

            }
            case Null: {
                handleAsNull();
                break;
            }
            case Unknown: {
                handleAsUnknown();
            }
            default: {
                handleAsUnknown();
            }
        }
    }

    private void handleAsDate() {
        DateTimeType d = new DateTimeType();
        d = t.castToDateTime(t);
        start = d.getValue();
        end = start;
        hardSort = true;
        text = Helper.dateToString(start);
    }

    private void handleAsAge(Context ctx) {
        Quantity a = new Quantity();
        a = t.castToQuantity(t);
        String disp = a.getDisplay();
        text = String.format("At age %s", disp);
        //TODO:  Use current context if patient
        // Grab the DOB
        // Quantity applied to DOB
        // Save a start
        a.getUnit();
        ctx.getDob();
    }

    private void handleAsPeriod() {
        Period p = new Period();
        p = t.castToPeriod(t);
        Date start = p.getStart();
        Date end = p.getEnd();
        //From s, s to e, To e
        if (start == null) {
            text = String.format("From %s", Helper.dateToString(start));
        } else if (end == null) {
            text = String.format("From %s to %s", Helper.dateToString(start), Helper.dateToString(end));
        } else {
            text = String.format("To %s", Helper.dateToString(end));
        }
    }

    private void handleAsRange() {
        //Do we need to handle this like age?
        Range r = new Range();
        r = t.castToRange(t);
        Quantity start = r.getLow();
        Quantity end = r.getHigh();
        text = String.format("Between %s and %s", start.getDisplay(), end.getDisplay());
        //TODO: Set Start and End
    }

    private void handleAsText() {
        StringType s = new StringType();
        s = t.castToString(t);
        text = s.getValue();
    }

    private void handleAsUnknown() {
        text = "a " + t.getClass().getName();
    }

    private void handleAsNull() {
        text = null;
    }

    enum DateType {Date, Period, Range, Age, Text, Unknown, Null}

}
