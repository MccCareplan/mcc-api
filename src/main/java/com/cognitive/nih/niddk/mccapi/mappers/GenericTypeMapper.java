package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Range;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class GenericTypeMapper {


    private static final int RANGE = 0;
    private static final int QUANTITY = 1;
    private static final int INTEGER = 2;
    private static final int CODEABLE_CONCEPT =3;
    private static final int STRING = 4;
    private static final int BOOLEAN = 5;
    private static final int RATIO = 6;
    private static final int REFERENCE = 7;

    private static Logger logger;

    private static HashMap<String,Integer> activeKeys = new HashMap<>();

    static {

        logger = LoggerFactory.getLogger(GenericTypeMapper.class);

        //Lifecycle Status
        //  proposed | planned | accepted | active | on-hold | completed | cancelled | entered-in-error | rejected




        //CodeableConcept, String, Boolean, Integer, Ratio
        activeKeys.put("Range",RANGE);
        activeKeys.put("Quantity",QUANTITY);
        activeKeys.put("Integer",INTEGER);
        activeKeys.put("CodeableConcept",CODEABLE_CONCEPT);
        activeKeys.put("String",STRING);
        activeKeys.put("Boolean",BOOLEAN);
        activeKeys.put("Ratio",RATIO);
        activeKeys.put("Reference",REFERENCE);

    }

    public static GenericType fhir2local(Type in, Context ctx)
    {
        GenericType out = new GenericType();
        String fhirType = in.fhirType();
        out.setValueType(fhirType);
        Integer localType = activeKeys.get(fhirType);
        if ( localType != null)
        {
            switch (localType.intValue())
            {
                case RANGE:
                {
                    out.setRange(fhir2local(in.castToRange(in),ctx));
                    break;
                }
                case RATIO:
                {
                    out.setRatio(fhir2local(in.castToRatio(in),ctx));
                    break;
                }
                case QUANTITY:
                {
                    out.setQuantity(fhir2local(in.castToQuantity(in),ctx));
                    break;
                }
                case CODEABLE_CONCEPT:
                {
                    out.setCodeableConceptValue(CodeableConceptMapper.fhir2local(in.castToCodeableConcept(in),ctx));
                    break;
                }
                case BOOLEAN:
                {
                    out.setBooleanValue(in.castToBoolean(in).booleanValue());
                    break;
                }
                case STRING:
                {
                    out.setStringValue(in.castToString(in).asStringValue());
                    break;
                }
                case INTEGER:
                {
                    out.setIntegerValue(in.castToInteger(in).getValue().intValue());
                    break;
                }
                default:
                {
                    logger.warn("Type {} regnogized but not yet handled");
                    break;
                }
            }
        }
        else
        {
            logger.warn("Unmapped type {}, ingoring",fhirType);
        }

        return out;
    }


    public static MccQuantity fhir2local(Quantity in, Context ctx)
    {
        MccQuantity out = new MccQuantity();
        out.setCode(in.getCode());
        if(in.getComparator()!= null) {
            out.setComparator(in.getComparator().toCode());
        }
        out.setSystem(in.getSystem());
        out.setUnit(in.getUnit());
        out.setValue(in.getValue().toPlainString());
        out.setDisplay(in.getDisplay());
        return out;
    }

    public static MccRatio fhir2local(Ratio in, Context ctx)
    {
        MccRatio out = new MccRatio();
        out.setDenominator(fhir2local(in.getDenominator(),ctx));
        out.setNumerator(fhir2local(in.getNumerator(),ctx));
        return out;
    }

    public static MccRange fhir2local(Range in, Context ctx)
    {
        MccRange out = new MccRange();
        out.setLow(fhir2local(in.getLow(),ctx));
        out.setHigh(fhir2local(in.getHigh(),ctx));
        return out;
    }
}
