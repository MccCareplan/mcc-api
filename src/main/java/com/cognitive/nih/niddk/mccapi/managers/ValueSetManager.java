package com.cognitive.nih.niddk.mccapi.managers;

import com.cognitive.nih.niddk.mccapi.data.MccValueSet;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
public class ValueSetManager {

    private static ValueSetManager singlton;

    public static ValueSetManager getValueSetManager() {
        if (singlton== null)
        {
            singlton = new ValueSetManager();
            singlton.init();
        }
        return singlton;}

    private static HashMap<String, ArrayList<String>>  codeMap = new HashMap<>();
    private static HashMap<String, MccValueSet> valueSetMap = new HashMap<>();


    public ValueSetManager()
    {
    }

    private void init()
    {
        //TODO: Enhance for database/local loading
        loadValueSets();
    }

    public void loadValueSets()
    {
        Set<String> list = getLoadList();
        for (String id:list)
        {
            loadValueSet(id);
        }
    }

    public static String getCodeKey(String system, String code)
    {
        return internalGetCodeKey(URLEncoder.encode(system, Charsets.UTF_8),code);
    }

    private static String internalGetCodeKey(String uuesystem, String code)
    {
        // Token|Code (Token is UUEncoded already)
        return uuesystem+"%7C"+code;
    }

    public ArrayList<String> findCodesValuesSets(String codeToken)
    {
        // TODO: Block if reloading
        return codeMap.get(codeToken);
    }

    public ArrayList<String> findCodesValuesSets(String system, String code)
    {
        // TODO: Block if reloading
        return codeMap.get(getCodeKey(system,code));
    }

    public MccValueSet findValueSet(String valueSetId)
    {
        // TODO: Block if reloading
        return valueSetMap.get(valueSetId);
    }

    public void  loadValueSet(String setId) {

        if (valueSetMap.containsKey(setId) == false) {
            MccValueSet vs = new MccValueSet();
            vs.setId(setId);
            String fileName = getFileName(setId);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                Reader in = new InputStreamReader(inputStream);
                try {
                    Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
                    for (CSVRecord record : records) {
                        String system = record.get("System");
                        //String version = record.get("Version");
                        String code = record.get("Code");
                        //String display = record.get("Display");

                        String key = getCodeKey(system, code);
                        if (codeMap.containsKey(key)) {
                            ArrayList<String> array = codeMap.get(key);
                            array.add(setId);
                            //Consider a String list here
                        } else {
                            ArrayList<String> array = new ArrayList<>();
                            array.add(setId);
                            codeMap.put(key, array);
                        }
                        vs.addCode(key);
                    }

                    valueSetMap.put(setId, vs);
                    //So we ned to both add the value code to the value set and add the value set
                } catch (IOException exp) {
                    log.error("Error loading valueset " + setId, exp);
                }
            }
        }
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

    public HashSet<String> getLoadList()
    {
        HashSet<String> out = new HashSet<>();
        //Todo + Scan directory or load from inventory
        out.add("2.16.840.1.113762.1.4.1222.159");   //CKD
        //out.add("2.16.840.1.113883.3.6929.3.1000");  //eGFR
        String loadFile = getFileName("valueset_loadlist");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(loadFile);
        if (inputStream != null) {
            Reader in = new InputStreamReader(inputStream);

            Iterable<CSVRecord> records = null;
            try {
                records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
                for (CSVRecord record : records) {
                    String oid = record.get("Oid");
                    out.add(oid);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }
        return out;
    }

    public void reloadAllValuesSets()
    {
        //Lock lookups
        //Reload
        codeMap.clear();
        valueSetMap.clear();
        loadValueSets();
    }

}
