/**
* Eliza
* Author: Charles Hayden
* http://www.chayden.net/eliza/Eliza.html
* Modified by Andres Colubri to use it as a Processing library
*/

package it.moondroid.chatbot.eliza;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 *  Eliza main class.
 *  Stores the processed script.
 *  Does the input transformations.
 */
public class Eliza {
  public Eliza(Context parent) {
		this.parent = parent;
		
		readDefaultScript();
  }
	
	public void dispose() {
		// anything in here will be called automatically when 
		// the parent applet shuts down. for instance, this might
		// shut down a thread used by this library.
		// note that this currently has issues, see bug #183
		// http://dev.processing.org/bugs/show_bug.cgi?id=183
    } 

    public boolean finished() {
        return finished;
    }

    /**
     *  Process a line of script input.
     */
    public void collect(String s) {
        String lines[] = new String[4];

        if (EString.match(s, "*reasmb: *", lines)) {
            if (lastReasemb == null) {
                Log.e("Eliza", "Error: no last reasemb");
                return;
            }
            lastReasemb.add(lines[1]);
        }
        else if (EString.match(s, "*decomp: *", lines)) {
            if (lastDecomp == null) {
                Log.e("Eliza", "Error: no last decomp");
                return;
            }
            lastReasemb = new ReasembList();
            String temp = new String(lines[1]);
            if (EString.match(temp, "$ *", lines)) {
                lastDecomp.add(lines[0], true, lastReasemb);
            } else {
                lastDecomp.add(temp, false, lastReasemb);
            }
        }
        else if (EString.match(s, "*key: * #*", lines)) {
            lastDecomp = new DecompList();
            lastReasemb = null;
            int n = 0;
            if (lines[2].length() != 0) {
                try {
                    n = Integer.parseInt(lines[2]);
                } catch (NumberFormatException e) {
                    Log.e("Eliza", "Number is wrong in key: " + lines[2]);
                }
            }
            keys.add(lines[1], n, lastDecomp);
        }
        else if (EString.match(s, "*key: *", lines)) {
            lastDecomp = new DecompList();
            lastReasemb = null;
            keys.add(lines[1], 0, lastDecomp);
        }
        else if (EString.match(s, "*synon: * *", lines)) {
            WordList words = new WordList();
            words.add(lines[1]);
            s = lines[2];
            while (EString.match(s, "* *", lines)) {
                words.add(lines[0]);
                s = lines[1];
            }
            words.add(s);
            syns.add(words);
        }
        else if (EString.match(s, "*pre: * *", lines)) {
            pre.add(lines[1], lines[2]);
        }
        else if (EString.match(s, "*post: * *", lines)) {
            post.add(lines[1], lines[2]);
        }
        else if (EString.match(s, "*initial: *", lines)) {
            initial = lines[1];
        }
        else if (EString.match(s, "*final: *", lines)) {
            finl = lines[1];
        }
        else if (EString.match(s, "*quit: *", lines)) {
            quit.add(" " + lines[1]+ " ");
        }
        else {
            Log.e("Eliza", "Unrecognized input: " + s);
        }
    }

    /**
     *  Print the stored script.
     */
    public void print() {
        if (printKeys) keys.print(0);
        if (printSyns) syns.print(0);
        if (printPrePost) {
            pre.print(0);
            post.print(0);
        }
        if (printInitialFinal) {
            Log.i("Eliza", "initial: " + initial);
            Log.i("Eliza", "final:   " + finl);
            quit.print(0);
            quit.print(0);
        }
    }

    /**
     *  Process a line of input.
     */
    public String processInput(String s) {
        String reply;
        //  Do some input transformations first.
        s = EString.translate(s, "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                                 "abcdefghijklmnopqrstuvwxyz");
        s = EString.translate(s, "@#$%^&*()_-+=~`{[}]|:;<>\\\"",
                                 "                          "  );
        s = EString.translate(s, ",?!", "...");
        //  Compress out multiple speace.
        s = EString.compress(s);
        String lines[] = new String[2];
        //  Break apart sentences, and do each separately.
        while (EString.match(s, "*.*", lines)) {
            reply = sentence(lines[0]);
            if (reply != null) return reply;
            s = EString.trim(lines[1]);
        }
        if (s.length() != 0) {
            reply = sentence(s);
            if (reply != null) return reply;
        }
        //  Nothing matched, so try memory.
        String m = mem.get();
        if (m != null) return m;

        //  No memory, reply with xnone.
        Key key = keys.getKey("xnone");
        if (key != null) {
            Key dummy = null;
            reply = decompose(key, s, dummy);
            if (reply != null) return reply;
        }
        //  No xnone, just say anything.
        return "I am at a loss for words.";
    }

	public boolean readDefaultScript()	{
		clearScript();
		
		return readScript("eliza.script");
	}	
    
	public boolean readScript(String script) {
		clearScript();
		
        String[] lines = loadStrings(script);
    if (lines == null || lines.length == 0) {
        Log.e("Eliza", "Cannot load Eliza script!");
      return false;
    } else {
      for (int i = 0; i < lines.length; i++) {
          Log.i("Eliza", "lines["+i+"]: "+lines[i]);
          collect(lines[i]);
      }
      return true;
    }

  }

    private String[] loadStrings(String script){
        ArrayList<String> out_list = new ArrayList<String>();
        try {
            parent.getAssets().openFd(script);
            InputStream is = parent.getAssets().open(script);
            // if file the available for reading
            if (is != null) {
                // prepare the file for reading
                InputStreamReader inputreader = new InputStreamReader(is);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                // read every line of the file into the line-variable, on line at the time
                do {
                    line = buffreader.readLine();
                    if(line != null){
                        out_list.add(line);
                    }

                } while (line != null);
            }

        }  catch (IOException e) {

            Log.e("Eliza", "IOException: "+e);
        }

        String[] outArr = new String[out_list.size()];
        outArr = out_list.toArray(outArr);

        return outArr;
    }
	
	void clearScript() {
		keys.clear();
		syns.clear();
		pre.clear();
		post.clear();
		initial = "";
		finl = "";
		quit.clear();
		keyStack.reset();
	}
	
    /**
     *  Process a sentence.
     *  (1) Make pre transformations.
     *  (2) Check for quit word.
     *  (3) Scan sentence for keys, build key stack.
     *  (4) Try decompositions for each key.
     */
    String sentence(String s) {
        s = pre.translate(s);
        s = EString.pad(s);
        if (quit.find(s)) {
            finished = true;
            return finl;
        }
        keys.buildKeyStack(keyStack, s);
        for (int i = 0; i < keyStack.keyTop(); i++) {
            Key gotoKey = new Key();
            String reply = decompose(keyStack.key(i), s, gotoKey);
            if (reply != null) return reply;
            //  If decomposition returned gotoKey, try it
            while (gotoKey.key() != null) {
                reply = decompose(gotoKey, s, gotoKey);
                if (reply != null) return reply;
            }
        }
        return null;
    }

    /**
     *  Decompose a string according to the given key.
     *  Try each decomposition rule in order.
     *  If it matches, assemble a reply and return it.
     *  If assembly fails, try another decomposition rule.
     *  If assembly is a goto rule, return null and give the key.
     *  If assembly succeeds, return the reply;
     */
    String decompose(Key key, String s, Key gotoKey) {
        String reply[] = new String[10];
        for (int i = 0; i < key.decomp().size(); i++) {
            Decomp d = (Decomp)key.decomp().elementAt(i);
            String pat = d.pattern();
            if (syns.matchDecomp(s, pat, reply)) {
                String rep = assemble(d, reply, gotoKey);
                if (rep != null) return rep;
                if (gotoKey.key() != null) return null;
            }
        }
        return null;
    }

    /**
     *  Assembly a reply from a decomp rule and the input.
     *  If the reassembly rule is goto, return null and give
     *    the gotoKey to use.
     *  Otherwise return the response.
     */
    String assemble(Decomp d, String reply[], Key gotoKey) {
        String lines[] = new String[3];
        d.stepRule();
        String rule = d.nextRule();
        if (EString.match(rule, "goto *", lines)) {
            //  goto rule -- set gotoKey and return false.
            gotoKey.copy(keys.getKey(lines[0]));
            if (gotoKey.key() != null) return null;
            Log.w("Eliza", "Goto rule did not match key: " + lines[0]);
            return null;
        }
        String work = "";
        while (EString.match(rule, "* (#)*", lines)) {
            //  reassembly rule with number substitution
            rule = lines[2];        // there might be more
            int n = 0;
            try {
                n = Integer.parseInt(lines[1]) - 1;
            } catch (NumberFormatException e) {
                Log.w("Eliza", "Number is wrong in reassembly rule " + lines[1]);
            }
            if (n < 0 || n >= reply.length) {
                Log.w("Eliza", "Substitution number is bad " + lines[1]);
                return null;
            }
            reply[n] = post.translate(reply[n]);
            work += lines[0] + " " + reply[n];
        }
        work += rule;
        if (d.mem()) {
            mem.save(work);
            return null;
        }
        return work;
    }
    
    Context parent;
    
    final boolean echoInput = false;
    final boolean printData = false;

    final boolean printKeys = false;
    final boolean printSyns = false;
    final boolean printPrePost = false;
    final boolean printInitialFinal = false;

    /** The key list */
    KeyList keys = new KeyList();
    /** The syn list */
    SynList syns = new SynList();
    /** The pre list */
    PrePostList pre = new PrePostList();
    /** The post list */
    PrePostList post = new PrePostList();
    /** Initial string */
    String initial = "Hello.";
    /** Final string */
    String finl = "Goodbye.";
    /** Quit list */
    WordList quit = new WordList();

    /** Key stack */
    KeyStack keyStack = new KeyStack();

    /** Memory */
    Mem mem = new Mem();

    DecompList lastDecomp;
    ReasembList lastReasemb;
    boolean finished = false;

    static final int success = 0;
    static final int failure = 1;
    static final int gotoRule = 2;
}
