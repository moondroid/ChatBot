package it.moondroid.chatbot.alice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Verbs {
    static Set<String> es = Utilities.stringSet("sh","ch","th","ss","x");
    static Set<String> ies = Utilities.stringSet("ly","ry","ny","fy","dy","py");
    static Set<String> ring = Utilities.stringSet("be","me","re","se","ve","de","le","ce","ze","ke","te","ge","ne","pe","ue");
    static Set<String> bing = Utilities.stringSet("ab","at","op","el","in","ur","op","er","un","in","it","et","ut","im","id","ol","ig");
    static Set<String> notBing = Utilities.stringSet("der","eat","ber","ain","sit","ait","uit","eet","ter","lop","ver","wer","aim","oid","eel","out","oin","fer","vel","mit");

    public static HashSet<String> irregular = new HashSet<String>();
    public static HashMap<String, String> be2was = new HashMap<String, String>();
    public static HashMap<String, String> be2been = new HashMap<String, String>();
    public static HashMap<String, String> be2is = new HashMap<String, String>();
    public static HashMap<String, String> be2being = new HashMap<String, String>();
    public static HashSet<String> allVerbs = new HashSet<String>();
    public static String endsWith(String verb, Set<String> endings) {
       for (String x : endings) { if (verb.endsWith(x)) return x;}
       return null;
    }
    public static String is(String verb) {
        String ending;
        if (irregular.contains(verb)) return be2is.get(verb);
        if (verb.endsWith("go")) return verb+"es";
        if ((ending = endsWith(verb, es)) != null) return verb+"es";
        if ((ending = endsWith(verb, ies)) != null) return verb.substring(0, verb.length()-1)+"ies";
        return verb+"s";
    }
    public static String was(String verb) {
        String ending;
        verb = verb.trim();
        if (verb.equals("admit")) return "admitted";
        if (verb.equals("commit")) return "committed";
        if (verb.equals("die")) return "died";
        if (verb.equals("agree")) return "agreed";
        if (verb.endsWith("efer")) return verb+"red";

        if (irregular.contains(verb)) {return be2was.get(verb);  }
        if ((ending = endsWith(verb, ies)) != null) {return verb.substring(0, verb.length()-1)+"ied";}
        if ((ending = endsWith(verb, ring)) != null) {return verb+"d"; }
        if ((ending = endsWith(verb, bing)) != null && (null==endsWith(verb, notBing))) {return verb+ending.substring(1,2)+"ed";}
        return verb+"ed";
    }
    public static String being(String verb) {
        String ending;
        if (irregular.contains(verb)) return be2being.get(verb);
        if (verb.equals("admit")) return "admitting";
        if (verb.equals("commit")) return "committing";
        if (verb.equals("quit")) return "quitting";
        if (verb.equals("die")) return "dying";
        if (verb.equals("lie")) return "lying";
        if (verb.endsWith("efer")) return verb+"ring";
        if ((ending = endsWith(verb, ring)) != null) { return verb.substring(0, verb.length()-1)+"ing";}
        if ((ending = endsWith(verb, bing)) != null && (null==endsWith(verb, notBing))) { return verb+ending.substring(1,2)+"ing";}

        return verb+"ing";
    }
    public static String been(String verb) {
        if (irregular.contains(verb)) return(be2been.get(verb));
        return was(verb);
    }


    public static void getIrregulars() {
        // Do, Did, Done, Does, Doing
        // be, was, been, is, being

        String irrFile = Utilities.getFile("c:/ab/data/irrverbs.txt");
        String[] triples = irrFile.split("\n");
        for (String x : triples) {
            x = x.toLowerCase();
            String[] triple = x.split(",");
            if  (triple.length==5) {
                irregular.add(triple[0]);
                allVerbs.add(triple[0]);
                be2was.put(triple[0], triple[1]);
                be2been.put(triple[0], triple[2]);
                be2is.put(triple[0], triple[3]);
                be2being.put(triple[0], triple[4]);

            }




        }
    }
    public static void makeVerbSetsMaps (Bot bot) {
        getIrregulars();
        String verbFile = Utilities.getFile("c:/ab/data/verb300.txt");
        String[] verbs = verbFile.split("\n");
        for (String verb : verbs) {
            allVerbs.add(verb);
        }
        AIMLSet be = new AIMLSet("be", bot);
        AIMLSet is = new AIMLSet("is", bot);
        AIMLSet was = new AIMLSet("was", bot);
        AIMLSet been = new AIMLSet("been", bot);
        AIMLSet being = new AIMLSet("being", bot);
        AIMLMap is2be = new AIMLMap("is2be", bot);
        AIMLMap be2is = new AIMLMap("be2is", bot);
        AIMLMap was2be = new AIMLMap("was2be", bot);
        AIMLMap be2was = new AIMLMap("be2was", bot);
        AIMLMap been2be = new AIMLMap("been2be", bot);
        AIMLMap be2been = new AIMLMap("be2been", bot);
        AIMLMap be2being = new AIMLMap("be2being", bot);
        AIMLMap being2be = new AIMLMap("being2be", bot);

        for (String verb : allVerbs) {
            String beForm = verb;
            String isForm = is(verb);
            String wasForm = was(verb);
            String beenForm = been(verb);
            String beingForm = being(verb);
            System.out.println(verb+","+isForm+","+wasForm+","+beingForm+","+beenForm);
            be.add(beForm);
            is.add(isForm);
            was.add(wasForm);
            been.add(beenForm);
            being.add(beingForm);
            be2is.put(beForm, isForm);
            is2be.put(isForm, beForm);
            be2was.put(beForm, wasForm);
            was2be.put(wasForm, beForm);
            be2been.put(beForm, beenForm);
            been2be.put(beenForm, beForm);
            be2being.put(beForm, beingForm);
            being2be.put(beingForm, beForm);


        }
        be.writeAIMLSet();
        is.writeAIMLSet();
        was.writeAIMLSet();
        been.writeAIMLSet();
        being.writeAIMLSet();
        be2is.writeAIMLMap();
        is2be.writeAIMLMap();
        be2was.writeAIMLMap();
        was2be.writeAIMLMap();
        be2been.writeAIMLMap();
        been2be.writeAIMLMap();
        be2being.writeAIMLMap();
        being2be.writeAIMLMap();
    }
}
