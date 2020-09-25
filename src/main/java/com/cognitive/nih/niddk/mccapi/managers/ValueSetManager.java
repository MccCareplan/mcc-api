package com.cognitive.nih.niddk.mccapi.managers;

import com.cognitive.nih.niddk.mccapi.data.MccValueSet;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class ValueSetManager {

    private static ValueSetManager singlton = new ValueSetManager();

    public static ValueSetManager getValueSetManager() {return singlton;}

    private static HashMap<String, ArrayList<String>>  codeMap = new HashMap<>();
    private static HashMap<String, MccValueSet> valueSetMap = new HashMap<>();

    public ValueSetManager()
    {

    }

    public void loadValueSets()
    {
        List<String> list = getLoadList();
        for (String id:list)
        {
            try {
                loadValueSet(id);
            }
            catch(IOException ioException)
            {
                log.error("Error loading value set: "+id,ioException);
            }
        }
    }

    public static String getCodeKey(String system, String code)
    {
        return internalGetCodeKey(URLEncoder.encode(system, Charsets.UTF_8),code);
    }

    private static String internalGetCodeKey(String uuesystem, String code)
    {
        return uuesystem+"|"+code;
    }

    public ArrayList<String> findCodesValuesSets(String codeToken)
    {
        return codeMap.get(codeToken);
    }
    public ArrayList<String> findCodesValuesSets(String system, String code)
    {
        return codeMap.get(getCodeKey(system,code));
    }

    public MccValueSet findValueSet(String valueSetId)
    {
        return valueSetMap.get(valueSetId);
    }

    public void  loadValueSet(String setId) throws IOException {

        MccValueSet vs = new MccValueSet();
        vs.setId(setId);
        String fileName = getFileName(setId);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        Reader in = new InputStreamReader(inputStream);

        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            String system = record.get("System");
            //String version = record.get("Version");
            String code = record.get("Code");
            //String display = record.get("Display");

            String key = getCodeKey(system,code);
            if (codeMap.containsKey(key))
            {
                ArrayList<String> array = codeMap.get(key);
                array.add(setId);
                //Consider a String list here
            }
            else
            {
                ArrayList<String > array = new ArrayList<>();
                array.add(setId);
                codeMap.put(key,array);
            }
            vs.addCode(key);
        }

        valueSetMap.put(setId,vs);
        //So we ned to both add the value code to the value set and add the value set a

    }
    public String getFileName(String valueSetId)
    {
        StringBuilder bld = new StringBuilder();
        bld.append("valuesets");
        bld.append(File.separator);
        bld.append(valueSetId);
        bld.append(".csv");
        return bld.toString();
    }

    public List<String> getLoadList()
    {
        ArrayList<String> out = new ArrayList<>();
        //Todo + Scan directory or load from inventory
        out.add("2.16.840.1.113762.1.4.1222.159");   //CKD
        out.add("2.16.840.1.113883.3.6929.3.1000");  //eGFR
        return out;
    }


}
