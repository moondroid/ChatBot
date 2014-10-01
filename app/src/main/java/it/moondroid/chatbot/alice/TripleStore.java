package it.moondroid.chatbot.alice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TripleStore {
    public int idCnt = 0;
    public String name = "unknown";
    public Chat chatSession;
    public Bot bot;
    public HashMap<String, Triple> idTriple = new HashMap<String, Triple>();
    public HashMap<String, String> tripleStringId = new HashMap<String, String>();
    public HashMap<String, HashSet<String>> subjectTriples = new HashMap<String, HashSet<String>>();
    public HashMap<String, HashSet<String>> predicateTriples = new HashMap<String, HashSet<String>>();
    public HashMap<String, HashSet<String>> objectTriples = new HashMap<String, HashSet<String>>();

    public TripleStore(String name, Chat chatSession) {
        this.name = name;
        this.chatSession = chatSession;
        this.bot = chatSession.bot;
    }
    public class Triple {
        public String id;
        public String subject;
        public String predicate;
        public String object;
        public Triple(String s, String p, String o) {
			Bot bot = TripleStore.this.bot;
            if (bot != null) {
                s = bot.preProcessor.normalize(s);
                p = bot.preProcessor.normalize(p);
                o = bot.preProcessor.normalize(o);
            }
            if (s != null && p != null && o != null)  {
                //System.out.println("New triple "+s+":"+p+":"+o);
                subject = s;
                predicate = p;
                object = o;
                id = name+idCnt++;
               // System.out.println("New triple "+id+"="+s+":"+p+":"+o);



            }
        }
    }
    public String mapTriple(Triple triple) {
        String id = triple.id;
        idTriple.put(id, triple);
        String s, p, o;
        s = triple.subject;
        p = triple.predicate;
        o = triple.object;

        s = s.toUpperCase();
        p = p.toUpperCase();
        o = o.toUpperCase();

        String tripleString = s+":"+p+":"+o;
        tripleString = tripleString.toUpperCase();

        if (tripleStringId.keySet().contains(tripleString)) {
            //System.out.println("Found "+tripleString+" "+tripleStringId.get(tripleString));
            return tripleStringId.get(tripleString); // triple already exists
        }
        else {
            //System.out.println(tripleString+" not found");
            tripleStringId.put(tripleString, id);

            HashSet<String> existingTriples;
            if (subjectTriples.containsKey(s)) existingTriples = subjectTriples.get(s);
            else existingTriples = new HashSet<String>();
            existingTriples.add(id);
            subjectTriples.put(s, existingTriples);

            if (predicateTriples.containsKey(p)) existingTriples = predicateTriples.get(p);
            else existingTriples = new HashSet<String>();
            existingTriples.add(id);
            predicateTriples.put(p, existingTriples);

            if (objectTriples.containsKey(o)) existingTriples = objectTriples.get(o);
            else existingTriples = new HashSet<String>();
            existingTriples.add(id);
            objectTriples.put(o, existingTriples);

            return id;
        }
    }


    public String unMapTriple(Triple triple) {
        String id = MagicStrings.undefined_triple;
        String s, p, o;
        s = triple.subject;
        p = triple.predicate;
        o = triple.object;

        s = s.toUpperCase();
        p = p.toUpperCase();
        o = o.toUpperCase();

        String tripleString = s+":"+p+":"+o;

        System.out.println("unMapTriple "+tripleString);
        tripleString = tripleString.toUpperCase();


        triple = idTriple.get(tripleStringId.get(tripleString));

        System.out.println("unMapTriple "+triple);
        if (triple != null) {
        id = triple.id;
        idTriple.remove(id);
        tripleStringId.remove(tripleString);

        HashSet<String> existingTriples;
        if (subjectTriples.containsKey(s)) existingTriples = subjectTriples.get(s);
        else existingTriples = new HashSet<String>();
        existingTriples.remove(id);
        subjectTriples.put(s, existingTriples);

        if (predicateTriples.containsKey(p)) existingTriples = predicateTriples.get(p);
        else existingTriples = new HashSet<String>();
        existingTriples.remove(id);
        predicateTriples.put(p, existingTriples);

        if (objectTriples.containsKey(o)) existingTriples = objectTriples.get(o);
        else existingTriples = new HashSet<String>();
        existingTriples.remove(id);
        objectTriples.put(o, existingTriples);
        }
        else id = MagicStrings.undefined_triple;

        return id;

    }

    public Set<String> allTriples () {
        return new HashSet<String>(idTriple.keySet());
    }

    public String addTriple(String subject, String predicate, String object) {
       if (subject == null || predicate == null || object == null) return MagicStrings.undefined_triple;
       Triple triple = new Triple(subject, predicate, object);
       String id = mapTriple(triple);
       return id;
    }
    public String deleteTriple(String subject, String predicate, String object) {
        if (subject == null || predicate == null || object == null) return MagicStrings.undefined_triple;
        if (MagicBooleans.trace_mode) System.out.println("Deleting "+subject+" "+predicate+" "+object);
        Triple triple = new Triple(subject, predicate, object);
        String id = unMapTriple(triple);
        return id;
    }
    public void printTriples() {
        for (String x : idTriple.keySet()) {
            Triple triple = idTriple.get(x);
            System.out.println(x+":"+triple.subject+":"+triple.predicate+":"+triple.object);
        }
    }

    HashSet<String> emptySet () {
        return new HashSet<String>();
    }


    public HashSet<String> getTriples(String s, String p, String o) {
        Set<String> subjectSet;
        Set<String> predicateSet;
        Set<String> objectSet;
        Set<String> resultSet;
        if (MagicBooleans.trace_mode) System.out.println("TripleStore: getTriples ["+idTriple.size()+"] "+s+":"+p+":"+o);
        //printAllTriples();
        if (s == null || s.startsWith("?")) {
            subjectSet = allTriples();
        }
            else {
                s = s.toUpperCase();
           // System.out.println("subjectTriples.keySet()="+subjectTriples.keySet());
           // System.out.println("subjectTriples.get("+s+")="+subjectTriples.get(s));
           // System.out.println("subjectTriples.containsKey("+s+")="+subjectTriples.containsKey(s));
                if (subjectTriples.containsKey(s)) subjectSet = subjectTriples.get(s);
                else subjectSet = emptySet();
            }
            // System.out.println("subjectSet="+subjectSet);

        if (p == null || p.startsWith("?")) {
            predicateSet = allTriples();
        }
            else {
                p = p.toUpperCase();
                if (predicateTriples.containsKey(p)) predicateSet = predicateTriples.get(p);
                else predicateSet = emptySet();
            }

        if (o == null || o.startsWith("?")) {
            objectSet = allTriples();
        }
            else {
                o = o.toUpperCase();
                if (objectTriples.containsKey(o)) objectSet = objectTriples.get(o);
                else objectSet = emptySet();
            }

        resultSet = new HashSet(subjectSet);
        resultSet.retainAll(predicateSet);
        resultSet.retainAll(objectSet);

        HashSet<String> finalResultSet = new HashSet(resultSet);

        //System.out.println("TripleStore.getTriples: "+finalResultSet.size()+" results");
        /* System.out.println("getTriples subjectSet="+subjectSet);
        System.out.println("getTriples predicateSet="+predicateSet);
        System.out.println("getTriples objectSet="+objectSet);
        System.out.println("getTriples result="+resultSet);*/

        return finalResultSet;
    }
    public HashSet<String> getSubjects(HashSet<String> triples) {
        HashSet<String> resultSet = new HashSet<String>();
        for (String id : triples) {
            Triple triple = idTriple.get(id);
            resultSet.add(triple.subject);
        }
        return resultSet;
    }
    public HashSet<String> getPredicates(HashSet<String> triples) {
        HashSet<String> resultSet = new HashSet<String>();
        for (String id : triples) {
            Triple triple = idTriple.get(id);
            resultSet.add(triple.predicate);
        }
        return resultSet;
    }
    public HashSet<String> getObjects(HashSet<String> triples) {
        HashSet<String> resultSet = new HashSet<String>();
        for (String id : triples) {
            Triple triple = idTriple.get(id);
            resultSet.add(triple.object);
        }
        return resultSet;
    }
    public String formatAIMLTripleList(HashSet<String> triples) {
        String result = MagicStrings.default_list_item;//"NIL"
        for (String x : triples) {
            result = x+" "+result;//"CONS "+x+" "+result;
        }
        return result.trim();
    }
    public String getSubject(String id) {
        if (idTriple.containsKey(id)) return idTriple.get(id).subject;
        else return "Unknown subject";
    }
    public String getPredicate(String id) {
        if (idTriple.containsKey(id)) return idTriple.get(id).predicate;
        else return "Unknown predicate";
    }
    public String getObject(String id) {
        if (idTriple.containsKey(id)) return idTriple.get(id).object;
        else return "Unknown object";
    }
    public String stringTriple(String id) {
        Triple triple = idTriple.get(id);
        return id+" "+triple.subject+" "+triple.predicate+" "+triple.object;
    }
    public void printAllTriples () {
        for (String id : idTriple.keySet()) {
            System.out.println(stringTriple(id));
        }
    }

    public HashSet<Tuple> select(HashSet<String> vars, HashSet<String> visibleVars, ArrayList<Clause> clauses) {
        HashSet<Tuple> result = new HashSet<Tuple>();
        try {

            Tuple tuple = new Tuple(vars, visibleVars);
            //System.out.println("TripleStore: select vars = "+tuple.printVars());
            result = selectFromRemainingClauses(tuple, clauses);
            if (MagicBooleans.trace_mode)
                for (Tuple t : result) {
                    System.out.println(t.printTuple());
                }

        }
        catch (Exception ex) {
            System.out.println("Something went wrong with select "+visibleVars);
            ex.printStackTrace();

        }
        return result;
    }


    public Clause adjustClause(Tuple tuple, Clause clause) {
        Set vars = tuple.getVars();
        String subj = clause.subj; String pred = clause.pred; String obj = clause.obj;
        Clause newClause = new Clause(clause);
        if (vars.contains(subj)) {
            String value = tuple.getValue(subj);
            if (!value.equals(MagicStrings.unbound_variable)) {/*System.out.println("adjusting "+subj+" "+value);*/ newClause.subj = value;}
        }
        if (vars.contains(pred)) {
            String value = tuple.getValue(pred);
            if (!value.equals(MagicStrings.unbound_variable)) {/*System.out.println("adjusting "+pred+" "+value);*/ newClause.pred= value;}
        }
        if (vars.contains(obj)) {
            String value = tuple.getValue(obj);
            if (!value.equals(MagicStrings.unbound_variable)) {/*System.out.println("adjusting "+obj+" "+value); */newClause.obj = value;}
        }
        return newClause;

    }

    public Tuple bindTuple(Tuple partial, String triple, Clause clause) {
        Tuple tuple = new Tuple(partial);
        if (clause.subj.startsWith("?")) tuple.bind(clause.subj, getSubject(triple));
        if (clause.pred.startsWith("?")) tuple.bind(clause.pred, getPredicate(triple));
        if (clause.obj.startsWith("?")) tuple.bind(clause.obj, getObject(triple));
        return tuple;
    }
    public HashSet<Tuple>  selectFromSingleClause(Tuple partial, Clause clause, Boolean affirm) {
        HashSet<Tuple> result = new HashSet<Tuple>();
        HashSet<String> triples = getTriples(clause.subj, clause.pred, clause.obj);
        //System.out.println("TripleStore: selected "+triples.size()+" from single clause "+clause.subj+" "+clause.pred+" "+clause.obj);
        if (affirm) {
            for (String triple : triples) {
                Tuple tuple = bindTuple(partial, triple, clause);
                result.add(tuple);
            }
        }
        else {
            if (triples.size()==0) result.add(partial);
        }
        return result;
    }

    public HashSet<Tuple> selectFromRemainingClauses(Tuple partial, ArrayList<Clause> clauses) {
        //System.out.println("TripleStore: partial = "+partial.printTuple()+" clauses.size()=="+clauses.size());
        HashSet<Tuple> result = new HashSet<Tuple>();
        Clause clause = clauses.get(0);
        clause = adjustClause(partial, clause);
        HashSet<Tuple> tuples = selectFromSingleClause(partial, clause, clause.affirm);
        if (clauses.size() > 1) {
          ArrayList<Clause> remainingClauses = new ArrayList<Clause>(clauses);
          remainingClauses.remove(0);
          for (Tuple tuple : tuples) {
             result.addAll(selectFromRemainingClauses(tuple, remainingClauses));
          }
        }
        else result = tuples;
        return result;
    }

}
