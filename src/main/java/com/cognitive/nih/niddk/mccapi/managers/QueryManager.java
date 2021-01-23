package com.cognitive.nih.niddk.mccapi.managers;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@Slf4j
public class QueryManager {

    private CompositeConfiguration cc;

    @Value("${hapi.logging.enabled:false}")
    private String enableLogging;
    @Value("${hapi.logging.queryexpansion:true}")
    private String log_query_expansion;
    private boolean logQueries = false;

    @PostConstruct
    public void init()
    {
        if ( Boolean.parseBoolean(enableLogging)  && Boolean.parseBoolean(log_query_expansion))
        {
            logQueries=true;
        }

        Configurations configs = new Configurations();
        try {
            Configuration base;
            Configuration override=null;

            base = configs.properties("fhirqueries.properties");
            String overidepath = "/usr/local/mcc-api/fhirqueries.properties";
            try {

                override = configs.properties(overidepath);
                log.info("Using a FHIR Query Override file:"+ overidepath);
            }
            catch(Exception e)
            {
                //Supress
                if (log.isDebugEnabled()) {
                    log.debug("Override file load error: " + overidepath, e);
                }
                else
                {
                    log.info("No override file accessible at "+overidepath);
                }
            }
            cc = new CompositeConfiguration();
            if (override != null) {
                cc.addConfiguration(override);
            }
            cc.addConfiguration(base);
        }
        catch(Exception e)
        {
            log.error("Error loading query configuration",e);
        }

    }

    public void reload()
    {
        init();
    }

    public String getQuery(String key)
    {
        return cc.getString(key);
    }

    public boolean doesQueryExist(String key)
    {
        String q = cc.getString(key);
        if ( q == null)
        {
            return false;
        }
        else if (q.isEmpty())
        {
            return false;
        }
        return true;
    }

    public void getQueryParameters(Map<String, String> map, WebRequest request)
    {
        Map<String, String[]> pm = request.getParameterMap();
        for (String k: pm.keySet())
        {
            //Let the first item take priority
            if (map.containsKey(k)==false) {
                map.put(k, request.getParameter(k));
            }
        }
    }


    public String setupQuery(String queryName, Map<String, String> values, WebRequest webRequest)
    {
        String callUrl = null;
        String templateURL = getQuery(queryName);
        if (templateURL != null && !templateURL.isBlank()) {
            getQueryParameters(values, webRequest);
            callUrl = StringSubstitutor.replace(templateURL, values, "{", "}");
        }
        if (logQueries)
        {
            log.info("FHIR Query - "+queryName+" expands to: "+callUrl);
        }
        return callUrl;
    }

    public String setupQuery(String queryName, Map<String, String> values)
    {
        String callUrl = null;
        String templateURL = getQuery(queryName);
        if (templateURL != null && !templateURL.isBlank()) {
            callUrl = StringSubstitutor.replace(templateURL, values, "{", "}");
        }
        if (logQueries)
        {
            log.info("FHIR Query - "+queryName+" expands to: "+callUrl);
        }
        return callUrl;
    }
}
