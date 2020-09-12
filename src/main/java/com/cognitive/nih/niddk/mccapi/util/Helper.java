package com.cognitive.nih.niddk.mccapi.util;

import lombok.NonNull;
import org.hl7.fhir.r4.model.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class Helper {
    private static final String dateFormat = "MM/dd/yyyy";
    private static final SimpleDateFormat fmtDate = new SimpleDateFormat(dateFormat);
    private static final String dateTimeFormat = "MM/dd/yyyy hh:mm";
    private static final SimpleDateFormat fmtDateTime = new SimpleDateFormat(dateFormat);

    private static final HashMap<String, String> unitsOfTime = new HashMap<>();
    private static final HashMap<String, String> unitOfTimePlural = new HashMap<>();

    static {
        unitsOfTime.put("s", "second");
        unitOfTimePlural.put("s", "seconds");
        unitsOfTime.put("min", "minute");
        unitOfTimePlural.put("min", "minutes");
        unitsOfTime.put("h", "hour");
        unitOfTimePlural.put("h", "hours");
        unitsOfTime.put("d", "day");
        unitOfTimePlural.put("d", "days");
        unitsOfTime.put("wk", "week");
        unitOfTimePlural.put("wk", "weeks");
        unitsOfTime.put("mo", "month");
        unitOfTimePlural.put("mo", "months");
        unitsOfTime.put("a", "year");
        unitOfTimePlural.put("a", "years");
    }

    public static String[] addressToLines(@NonNull Address address) {
        int lines = address.getLine().size() + 1;
        if (address.hasCountry()) lines++;

        String[] out = new String[lines];
        int i = 0;
        for (StringType line : address.getLine()) {
            out[i] = line.toString();
            i++;
        }
        StringBuffer csz = new StringBuffer();
        if (address.hasCountry()) {
            //Use Local conventions (USA) for now - Check https://www.upu.int/en/Home for more info
            if (address.getCountry().compareTo("US") == 0) {
                csz.append(address.getCity());
                csz.append(" ");
                csz.append(address.getState());
                csz.append(", ");
                csz.append(address.getPostalCode());
            } else {
                if (address.hasCity()) {
                    csz.append(address.getCity());
                    csz.append(" ");
                }
                if (address.hasDistrict()) {
                    csz.append(address.getDistrict());
                    csz.append(" ");
                }
                if (address.hasState()) {
                    csz.append(address.getState());
                }
                if (address.hasPostalCode()) {
                    csz.append(" ");
                    csz.append(address.getPostalCode());
                }
            }
            out[i] = csz.toString();
            i++;
            out[i] = address.getCountry();
        } else {
            //Use Local conventions (USA)
            csz.append(address.getCity());
            csz.append(" ");
            csz.append(address.getState());
            csz.append(", ");
            csz.append(address.getPostalCode());
            out[i] = csz.toString();
        }
        return out;
    }

    public static String addressToString(@NonNull Address address) {
        if (address.hasText()) {
            return address.getText();
        } else {
            StringBuffer out = new StringBuffer();
            String[] lines = addressToLines(address);
            for (int i = 0; i < lines.length; i++) {
                if (i != 0) {
                    out.append(" ");
                }
                out.append(lines[i]);
            }
            return out.toString();
        }
    }

    public static String annotationToString(@NonNull Annotation a) {

        StringBuffer out = new StringBuffer();
        boolean bAddLine = false;
        if (a.hasTime()) {
            out.append(Helper.dateTimeToString(a.getTime()));
            out.append(" ");
        }
        if (a.hasAuthor()) {
            if (a.hasAuthorStringType()) {
                out.append(a.getAuthorStringType().getValue());
                out.append(" ");
            } else {
                //We have an Author Reference
                //TODO: Call a Reference resolver
            }
        }
        //TOOO: Deal with Markdown
        out.append(a.getText());
        return out.toString();
    }

    public static String annotationsToString(List<Annotation> annotations) {

        StringBuffer out = new StringBuffer();
        if (annotations != null) {
            boolean bAddLine = false;
            for (Annotation a : annotations) {
                if (bAddLine) {
                    out.append("\n");
                }
                //TOOO: Deal with Markdown
                out.append(annotationToString(a));
                bAddLine = true;
            }
        }
        return out.toString();
    }

    public static String[] annotationsToStringList(@NonNull List<Annotation> annotations) {
        String[] out = new String[annotations.size()];
        int index = 0;
        for (Annotation a : annotations) {
            out[index] = annotationToString(a);
            index++;
        }
        return out;
    }

    public static String bigDecimalAsString(@NonNull BigDecimal in) {
        //For now
        return in.toPlainString();
    }

    public static boolean containsCode(CodeableConcept concept, String system, String code) {
        String out = null;
        for (Coding cd : concept.getCoding()) {
            if (cd.getSystem().compareTo(system) == 0) {
                if (cd.getCode().compareTo(code) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsCode(CodeableConcept concept, String code) {
        String out = null;
        for (Coding cd : concept.getCoding()) {
            if (cd.getCode().compareTo(code) == 0) {
                return true;
            }
        }
        return false;
    }

    public static String dateTimeToString(Date d) {
        if (d == null) {
            return null;
        }
        //TODO: Deal with TimeZone
        return fmtDateTime.format(d);
    }

    public static String dateToString(Date d) {
        if (d == null) {
            return null;
        }
        //TODO: Deal with TimeZone for UTC - fmtDate.setTimeZone(TimeZone.getTimeZone("UTC"))
        return fmtDate.format(d);
    }

    public static String durationToString(@NonNull Duration duration) {
        if (duration.hasDisplay()) {
            return duration.getDisplay();
        }

        StringBuffer out = new StringBuffer();

        if (duration.hasComparator()) {
            out.append(duration.getComparator().getDisplay());
        }
        if (duration.hasValue()) {
            out.append(duration.getValue().toPlainString());
        }
        if (duration.hasUnit()) {
            out.append(duration.getUnit());
        }
        return out.toString();
    }

    public static List<Address> filterToCurrentAddresses(List<Address> addresses) {
        Date now = new Date();
        //Use an array list since this is a filter and it will not get larger
        ArrayList<Address> out = new ArrayList<>();
        if (addresses != null) {
            if (addresses.size() > 0) {
                out.ensureCapacity(addresses.size());
                for (Address a : addresses) {
                    if (a.hasPeriod()) {
                        if (isInPeriod(a.getPeriod(), now)) {
                            out.add(a);
                        }
                    } else {
                        out.add(a);
                    }
                }
            }
        }
        return out;
    }

    public static List<ContactPoint> filterToCurrentContactPoints(List<ContactPoint> points) {
        Date now = new Date();

        //Use an array list since this is a filter and it will not get larger
        ArrayList<ContactPoint> out = new ArrayList<>();
        if (points != null) {
            out.ensureCapacity(points.size());

            for (ContactPoint a : points) {
                if (a.hasPeriod()) {
                    if (isInPeriod(a.getPeriod(), now)) {
                        out.add(a);
                    }
                } else {
                    out.add(a);
                }
            }
        }
        return out;
    }

    public static Address findBestAddress(List<Address> addresses, @NonNull String type) {
        Address out = null;
        if (addresses != null) {
            List<Address> filtered = filterToCurrentAddresses(addresses);
            if (filtered.size() > 0) {
                //Default to the first
                out = filtered.get(0);
                for (Address a : filtered) {
                    if (a.hasType()) {
                        if (a.getType().toCode().compareTo(type) == 0) {
                            out = a;
                            break;
                        }
                    }
                }
            }
        }
        return out;
    }

    public static ContactPoint findBestContactByType(Map<String, List<ContactPoint>> cpBySystem, String type, String usePriority) {
        String[] uses = usePriority.split("|");
        ContactPoint best = null;
        List<ContactPoint> contacts = cpBySystem.get(type);
        if (contacts != null) {
            if (contacts.size() > 0) {
                if (Helper.hasRank(contacts)) {
                    best = Helper.getHighestPriority(contacts);
                } else {
                    Map<String, List<ContactPoint>> contactsByUse = Helper.organizeContactTypesByUse(contacts);
                    for (String use : uses) {
                        if (contactsByUse.containsKey(use)) {
                            best = contactsByUse.get(use).get(0);
                            break;
                        }
                    }
                    if (best == null)
                        best = contacts.get(0);
                }
            }
        }
        return best;
    }

    public static String getBestName(@NonNull List<HumanName> names) {
        String name;
        HumanName bestName = null;
        //Locate the best name
        for (HumanName nm : names) {
            if (nm.getUse() == HumanName.NameUse.OFFICIAL) {
                bestName = nm;
                break;
            }
        }
        if (bestName == null) {
            bestName = names.get(0);
        }

        name = getName(bestName);

        return name;
    }

    public static String getCodingDisplayExtensionAsString(DomainResource r, String extURL, String field, String defValue) {
        Extension extb = r.getExtensionByUrl(extURL);
        if (extb != null) {

            Extension ext = extb.getExtensionByUrl(field);
            if (ext != null) {
                Coding cd = ext.castToCoding(ext.getValue());
                if (cd.hasDisplay()) return cd.getDisplay();
                if (cd.hasCode()) {
                    //TODO: - Add code lookup and log conformance warning
                    return cd.getCode();
                }
                return defValue;
            }
            return defValue;
        } else {
            //TODO: Log as missing extension
            return defValue;
        }
    }

    public static Coding getCodingForSystem(CodeableConcept concept, String system) {
        Coding out = null;
        List<Coding> codings = concept.getCoding();
        for (Coding code : codings) {
            String cs = code.getSystem();
            if (cs != null) {
                if (cs.compareTo(system) == 0) {
                    out = code;
                    break;
                }
            }
        }
        return out;
    }

    public static String getConceptCode(CodeableConcept concept, String system) {
        String out = null;
        for (Coding cd : concept.getCoding()) {
            if (cd.getSystem().compareTo(system) == 0) {
                out = cd.getCode();
                break;
            }
        }
        return out;
    }

    public static String getConceptCode(CodeableConcept concept, Set<String> system) {
        String out = null;
        for (Coding cd : concept.getCoding()) {
            if (system.contains(cd.getSystem())) {
                out = cd.getCode();
            }
        }
        return out;
    }

    public static String getConceptCodes(CodeableConcept[] concepts, String system) {
        StringBuffer out = new StringBuffer();
        boolean extra = false;
        for (CodeableConcept c : concepts) {
            if (extra) {
                out.append(",");
            }
            out.append(getConceptCode(c, system));
            extra = true;
        }
        return out.toString();
    }

    public static String getConceptCodes(CodeableConcept[] concepts, Set<String> system) {
        StringBuffer out = new StringBuffer();
        boolean extra = false;
        for (CodeableConcept c : concepts) {
            if (extra) {
                out.append(",");
            }
            out.append(getConceptCode(c, system));
            extra = true;
        }
        return out.toString();
    }

    public static String getConceptDisplayString(CodeableConcept concept) {
        if (!concept.getText().isBlank()) {
            return concept.getText();
        }
        String out = null;
        //Search for any coding text
        for (Coding cd : concept.getCoding()) {
            if (cd.hasDisplay()) {
                out = cd.getDisplay();
                break;
            }
        }
        if (out == null) {
            //No coding text was found (grrr)
            //Now we have a few choices -
            //  1) Grab and try to look up a description for each code
            //  2) Build a System, Code representtion
            //  3) return a default value
            //  4) return null
            //
            // For Diagnositic purposes we are going to...
            Coding cd = concept.getCodingFirstRep();
            out = String.format("%s {%s}", cd.getCode(), cd.getSystem());
        }
        return out;
    }

    public static HashSet<String> getConceptSet(CodeableConcept[] concepts, String system) {
        HashSet<String> out = new HashSet<>();
        for (CodeableConcept c : concepts) {
            out.add(getConceptCode(c, system));
        }
        return out;
    }

    public static String getConceptsAsDisplayString(List<CodeableConcept> concepts) {
        StringBuffer out = new StringBuffer();
        boolean extra = false;
        for (CodeableConcept c : concepts) {
            if (extra) {
                out.append(",");
            }
            out.append(getConceptDisplayString(c));
            extra = true;
        }
        return out.toString();
    }

    public static String getConceptsAsDisplayString(CodeableConcept[] concepts) {
        StringBuffer out = new StringBuffer();
        boolean extra = false;
        for (CodeableConcept c : concepts) {
            if (extra) {
                out.append(",");
            }
            out.append(getConceptDisplayString(c));
            extra = true;
        }
        return out.toString();
    }

    public static ContactPoint getHighestPriority(List<ContactPoint> points) {
        ContactPoint out = null;
        if (points != null) {
            if (points.size() > 0) {
                out = points.get(0);
                if (out.hasRank())
                    for (ContactPoint c : points) {
                        if (c.hasRank()) {
                            if (out.hasRank()) {
                                if (c.getRank() < out.getRank()) {
                                    out = c;
                                }
                            } else {
                                out = c;
                            }
                        }
                    }
            }
        }
        return null;

    }

    public static String getName(@NonNull HumanName nm) {
        String name;
        if (nm.hasText()) {
            name = nm.getText();
        } else {
            name = nm.getNameAsSingleString();
        }
        return name;

    }

    public static boolean hasRank(List<ContactPoint> points) {
        if (points != null) {
            for (ContactPoint c : points) {
                if (c.hasRank()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isInPeriod(@NonNull Period p, @NonNull Date d) {
        //Inclusive check
        if (p.hasStart()) {
            if (d.compareTo(p.getStart()) < 0) {
                return false;
            }
        }
        if (p.hasEnd()) {
            if (d.compareTo(p.getEnd()) > 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isReferenceOfType(Reference ref, String type) {
        return getReferenceType(ref).compareTo(type) ==0;
    }

    public static String getReferenceType(Reference ref) {

        if (ref == null) {
            return "Null";
        }

        if (ref.hasType()) {
            return ref.getType();
        }

        if (ref.hasReference()) {
            String[] parts = ref.getReference().split("/");
            if (parts.length > 1) {
                String fndtype = parts[parts.length - 2];
                return fndtype;
            }

        }
        return "Unknown";
    }

    public static String getIdString(IdType ide)
    {
        StringBuffer addr = new StringBuffer();
        addr.append(ide.getResourceType());
        addr.append("/");
        addr.append(ide.getIdPart());
        return addr.toString();
    }

    public static Map<String, List<ContactPoint>> organizeContactTypesBySystem(List<ContactPoint> points) {
        HashMap<String, List<ContactPoint>> out = new HashMap<>();

        if (points != null) {
            for (ContactPoint cp : points) {
                String system = cp.hasSystem() ? cp.getSystem().toCode() : "other";
                List<ContactPoint> l;
                if (out.containsKey(system)) {
                    l = out.get(system);
                } else {
                    l = new ArrayList<>(points.size());
                    out.put(system, l);
                }
                l.add(cp);
            }
        }
        return out;
    }

    public static Map<String, List<ContactPoint>> organizeContactTypesByUse(List<ContactPoint> points) {
        HashMap<String, List<ContactPoint>> out = new HashMap<>();

        if (points != null) {
            for (ContactPoint cp : points) {
                String use = cp.hasUse() ? cp.getUse().toCode() : "other";
                List<ContactPoint> l;
                if (out.containsKey(use)) {
                    l = out.get(use);
                } else {
                    l = new ArrayList<>(points.size());
                    out.put(use, l);
                }
                l.add(cp);
            }
        }
        return out;
    }

    public static String translateRepeat(@NonNull Timing.TimingRepeatComponent repeat) {
        StringBuilder out = new StringBuilder();

        if (repeat.getFrequency() > 0) {
            if (repeat.getFrequencyMax() > repeat.getFrequency()) {
                out.append(repeat.getFrequency());
                out.append("-");
                out.append(repeat.getFrequencyMax());
                out.append(" times");
            } else {
                if (repeat.getFrequency() == 1) {
                    out.append("Every");
                } else {
                    out.append(repeat.getFrequency());
                    out.append(" times");
                    if (repeat.hasPeriod()) {
                        out.append(" per");
                    }
                }
                if (repeat.getPeriod() != null) {
                    BigDecimal period = repeat.getPeriod();
                    boolean plural = period.compareTo(BigDecimal.ONE) != 0;
                    if (repeat.hasPeriodMax()) {
                        out.append(" ");
                        out.append(period.toPlainString());
                        out.append("-");
                        out.append(repeat.getPeriodMax().toPlainString());
                    } else {
                        if (plural) {
                            out.append(" ");
                            out.append(period.toPlainString());
                        }
                    }
                    if (repeat.hasPeriodUnit()) {
                        out.append(" ");
                        out.append(unitOfTime(repeat.getPeriodUnit().toCode(), plural));
                    }

                }

            }
        }

        boolean pluralDuration = false;
        if (repeat.getDuration() != null) {
            out.append(" for ");
            BigDecimal one = BigDecimal.valueOf(1);
            BigDecimal d = repeat.getDuration();

            if (d.compareTo(one) > 0) {
                pluralDuration = true;
            }
            //TODO: Deal with Fractions
            out.append(repeat.getDuration().toPlainString());
            if (repeat.getDurationMax() != null) {
                out.append(" to ");
                if (repeat.getDurationMax().compareTo(one) > 0) {
                    pluralDuration = true;
                }
                out.append(repeat.getDurationMax().toPlainString());
            }

        }
        if (repeat.hasDurationUnitElement()) {
            out.append(" ");
            out.append(unitOfTime(repeat.getDurationUnitElement().getCode(), pluralDuration));
        }
        return out.toString();
    }

    public static String translateTiming(@NonNull Timing timing) {
        timing.getCode().getText();
        StringBuilder out = new StringBuilder();
        if (timing.hasRepeat()) {
            out.append(translateRepeat(timing.getRepeat()));
        }


        if (out.length() == 0 && timing.hasCode()) {
            return timing.getCode().getText();
        }
        return out.toString();
    }

    public static String unitOfTime(@NonNull String code, boolean plural) {
        if (plural) {
            return unitOfTimePlural.get(code);
        } else {
            return unitsOfTime.get(code);
        }
    }
}
