package it.moondroid.chatbot.alice;
public class Clause {
    public String subj;
    public String pred;
    public String obj;
    public Boolean affirm;
    public Clause(String s, String p, String o) {
        this(s, p, o, true);
    }
    public Clause(String s, String p, String o, Boolean affirm) {
        subj = s;
        pred = p;
        obj = o;
        this.affirm = affirm;
    }
    public Clause(Clause clause) {
        this(clause.subj, clause.pred, clause.obj, clause.affirm);
    }
}
