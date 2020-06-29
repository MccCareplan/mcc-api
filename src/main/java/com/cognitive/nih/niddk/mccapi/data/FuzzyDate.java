package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.*;

import java.util.Date;

public class FuzzyDate {


    private Type t;
    private String text;
    private Date start;
    private Date end;
    private boolean hardSort;
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
            else if (c == Quantity.class) return DateType.Age;  //Assume a quanity is an age here
            else if (c == StringType.class) return DateType.Text;
            else return DateType.Unknown;
        }
        return DateType.Null;
    }

    public static String buildString(Type t, Context ctx) {
        //TODO: Deal with abstarcting sorting for start and end
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
        //TODO:  Use current context if p
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
        Range r = new Range();
        r = t.castToRange(t);
        Quantity start = r.getLow();
        Quantity end = r.getHigh();
        text = String.format("Between %s and %s", start.getDisplay(), end.getDisplay());
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
