/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.util;

import lombok.extern.slf4j.Slf4j;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
public class UCUMHelper {

    static private UCUMHelper singleton = new UCUMHelper();
    private UcumEssenceService service;

    static public UCUMHelper getHelper() {return singleton;}
    public UcumEssenceService getService()
    {
        if (service ==  null)
        {
            try {
                service = new UcumEssenceService(getClass().getClassLoader().getResourceAsStream("ucum-essence.xml"));

            } catch (UcumException e) {
                log.error("Unable to load ucum-essence",e);
            }
        }
        return service;
    }

    /**
     *
     * @param value in umol/L
     * @param molmass the molecular mass g/mol (Da)  [e.g. SCr = 113.12 g/mol]
     * @return converted value in mg/dL
     */
    static public BigDecimal umol_L_2_mg_dL(BigDecimal value, BigDecimal molmass)
    {
        //Normailze molmass
        BigDecimal factor = molmass.divide(BigDecimal.valueOf(10000),MathContext.DECIMAL64);
        MathContext outputCtx = new MathContext(2);
        return factor.multiply(value).round(outputCtx);
    }

    /**
     *
     * @param value
     * @param molmass
     * @return
     */
    static public BigDecimal mg_dl_2_umol_L(BigDecimal value, BigDecimal molmass)
    {
        //Normailze molmass
        BigDecimal base = molmass.divide(BigDecimal.valueOf(10000));
        BigDecimal factor = BigDecimal.valueOf(1).divide(base, MathContext.DECIMAL64);
        MathContext outputCtx = new MathContext(4);
        return factor.multiply(value).round(outputCtx);
    }
}
