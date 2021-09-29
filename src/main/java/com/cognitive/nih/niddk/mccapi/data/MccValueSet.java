/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.managers.ValueSetManager;
import lombok.Data;

import java.util.HashSet;

@Data
public class MccValueSet {
  private String id;

  private HashSet<String> codes = new HashSet<>();

  public void addCode(String code)
  {
      codes.add(code);
  }

  public boolean hasCode(String system, String code)
  {
      String key = ValueSetManager.getCodeKey(system,code);
      return codes.contains(key);
  }

  public String asQueryString()
  {
        StringBuilder bld = new StringBuilder();
        for (String code: codes) {
            if (bld.length()>0)
            {
                bld.append(",");
            }
            bld.append(code);
        }
      return bld.toString();
  }
}
