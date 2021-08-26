package com.cognitive.nih.niddk.mccapi.util;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class FHIRHelperTest {

    @Test
    void dateTimeToString() {
        Date d = new Date();
        FHIRHelper.dateTimeToString(d);
    }

    @Test
    void dateToString() {
    }
}
