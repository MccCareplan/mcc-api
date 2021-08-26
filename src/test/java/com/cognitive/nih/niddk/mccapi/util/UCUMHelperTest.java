package com.cognitive.nih.niddk.mccapi.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UCUMHelperTest {

    @Test
    void umol_L_2_mg_dL() {
        BigDecimal ScrMolMass = BigDecimal.valueOf(113.12);
        BigDecimal test = BigDecimal.valueOf(65.42);
        BigDecimal result = UCUMHelper.umol_L_2_mg_dL(test,ScrMolMass);
        assertTrue(result.compareTo(BigDecimal.valueOf(0.74))==0);
    }

    @Test
    void mg_dl_2_umol_L() {
        BigDecimal ScrMolMass = BigDecimal.valueOf(113.12);
        BigDecimal test = BigDecimal.valueOf(0.74);
        BigDecimal result = UCUMHelper.mg_dl_2_umol_L(test,ScrMolMass);
        assertTrue(result.compareTo(BigDecimal.valueOf(65.42))==0);
        test = BigDecimal.valueOf(1.35);
        result = UCUMHelper.mg_dl_2_umol_L(test,ScrMolMass);
        assertTrue(result.compareTo(BigDecimal.valueOf(119.3))==0);
    }
}
