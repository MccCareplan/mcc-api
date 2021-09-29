/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.util;

public class JavaHelper {

    public static StringBuilder addStringToBufferWithSep(StringBuilder buf, String str,String sep)
    {
        if (buf.length()>0)
        {
            buf.append(sep);
        }
        buf.append(str);

        return buf;
    }
}
