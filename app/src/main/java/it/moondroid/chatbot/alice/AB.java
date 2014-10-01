package it.moondroid.chatbot.alice;
/* Program AB Reference AIML 2.0 implementation
        Copyright (C) 2013 ALICE A.I. Foundation
        Contact: info@alicebot.org

        This library is free software; you can redistribute it and/or
        modify it under the terms of the GNU Library General Public
        License as published by the Free Software Foundation; either
        version 2 of the License, or (at your option) any later version.

        This library is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
        Library General Public License for more details.

        You should have received a copy of the GNU Library General Public
        License along with this library; if not, write to the
        Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
        Boston, MA  02110-1301, USA.
*/


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import it.moondroid.chatbot.alice.utils.IOUtils;

public class AB {
    /**
     * Experimental class that analyzes log data and suggests
     * new AIML patterns.
     *
     */
    public boolean shuffle_mode = true;
    public boolean sort_mode = !shuffle_mode;
    public boolean filter_atomic_mode = false;
    public boolean filter_wild_mode = false;
    public boolean offer_alice_responses = true;

    public String logfile = MagicStrings.root_path+"/data/"+MagicStrings.ab_sample_file; //normal.txt";

    public int runCompletedCnt;
    public Bot bot;
    public Bot alice;
    AIMLSet passed;
    AIMLSet testSet;

    public final Graphmaster inputGraph;
    public final Graphmaster patternGraph;
    public final Graphmaster deletedGraph;
    public ArrayList<Category> suggestedCategories;
    public static int limit = 500000;
    public AB(Bot bot, String sampleFile) {
        MagicStrings.ab_sample_file = sampleFile;
        logfile = MagicStrings.root_path+"/data/"+MagicStrings.ab_sample_file;
        System.out.println("AB with sample file "+logfile);
        this.bot = bot;
        this.inputGraph = new Graphmaster(bot, "input");
        this.deletedGraph = new Graphmaster(bot, "deleted");
        this.patternGraph = new Graphmaster(bot, "pattern");
        for (Category c : bot.brain.getCategories()) patternGraph.addCategory(c);
        this.suggestedCategories = new ArrayList<Category>();
        passed = new AIMLSet("passed", bot);
        testSet = new AIMLSet("1000", bot);
        readDeletedIFCategories();
    }
    /**
     * Calculates the botmaster's productivity rate in
     * categories/sec when using Pattern Suggestor to create content.
     *
     * @param  runCompletedCnt  number of categories completed in this run
     * @param  timer tells elapsed time in ms
     * @see    it.moondroid.chatbot.alice.AB
     */

    public void productivity (int runCompletedCnt, Timer timer) {
        float time = timer.elapsedTimeMins();
        System.out.println("Completed "+runCompletedCnt+" in "+time+" min. Productivity "+(float)runCompletedCnt/time+" cat/min");
    }
    public void readDeletedIFCategories() {
        bot.readCertainIFCategories(deletedGraph, MagicStrings.deleted_aiml_file);
        if (MagicBooleans.trace_mode) System.out.println("--- DELETED CATEGORIES -- read "+deletedGraph.getCategories().size()+" deleted categories");
    }
    public void writeDeletedIFCategories() {
        System.out.println("--- DELETED CATEGORIES -- write");
        bot.writeCertainIFCategories(deletedGraph, MagicStrings.deleted_aiml_file);
        System.out.println("--- DELETED CATEGORIES -- write "+deletedGraph.getCategories().size()+" deleted categories");

    }
       /** saves a new AIML category and increments runCompletedCnt
        *
        *
        * @param pattern    the category's pattern (that and topic = *)
        * @param template   the category's template
        * @param filename   the filename for the category.
        */
    public void saveCategory(String pattern, String template, String filename) {
        String that = "*";
        String topic = "*";
        Category c = new Category(0, pattern, that, topic, template, filename);

        if (c.validate()) {
            bot.brain.addCategory(c);
         // bot.categories.add(c);
            bot.writeAIMLIFFiles();
            runCompletedCnt++;
        }
        else System.out.println("Invalid Category "+c.validationMessage);
    }

       /** mark a category as deleted
        *
        * @param c       the category
        */
    public void deleteCategory(Category c) {
        c.setFilename(MagicStrings.deleted_aiml_file);
        c.setTemplate(MagicStrings.deleted_template);
        deletedGraph.addCategory(c);
        System.out.println("--- bot.writeDeletedIFCategories()");
        writeDeletedIFCategories();
     }

       /** skip a category.  Make the category as "unfinished"
        *
        *
        * @param c   the category
        */
    public void skipCategory(Category c) {
       /* c.setFilename(MagicStrings.unfinished_aiml_file);
        c.setTemplate(MagicStrings.unfinished_template);
        bot.unfinishedGraph.addCategory(c);
        System.out.println(bot.unfinishedGraph.getCategories().size() + " unfinished categories");
        bot.writeUnfinishedIFCategories();*/
    }
    public void abwq() {
        Timer timer = new Timer();
        timer.start();
        classifyInputs(logfile);
        System.out.println(timer.elapsedTimeSecs() + " classifying inputs");
        bot.writeQuit();
    }
    /** read sample inputs from filename, turn them into Paths, and
     * add them to the graph.
     *
     * @param filename file containing sample inputs
     */
    public void graphInputs (String filename) {
        int count = 0;
        try{
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null  && count < limit)   {
                //strLine = preProcessor.normalize(strLine);
                Category c = new Category(0, strLine, "*", "*", "nothing", MagicStrings.unknown_aiml_file);
                Nodemapper node = inputGraph.findNode(c);
                if (node == null) {
                    inputGraph.addCategory(c);
                    c.incrementActivationCnt();
                }
                else node.category.incrementActivationCnt();
                count++;
                //System.out.println("Root branches="+g.root.size());
            }
            //Close the input stream
            br.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    static int leafPatternCnt = 0;
    static int starPatternCnt = 0;

    /** find suggested patterns in a graph of inputs
     *
     */
    public void findPatterns() {
        findPatterns(inputGraph.root, "");
        System.out.println(leafPatternCnt+ " Leaf Patterns "+starPatternCnt+" Star Patterns");
    }

    /** find patterns recursively
     *
     * @param node                      current graph node
     * @param partialPatternThatTopic   partial pattern path
     */
    void findPatterns(Nodemapper node, String partialPatternThatTopic) {
        if (NodemapperOperator.isLeaf(node)) {
            //System.out.println("LEAF: "+node.category.getActivationCnt()+". "+partialPatternThatTopic);
            if (node.category.getActivationCnt() > MagicNumbers.node_activation_cnt) {
                //System.out.println("LEAF: "+node.category.getActivationCnt()+". "+partialPatternThatTopic+" "+node.shortCut);    //Start writing to the output stream
                leafPatternCnt ++;
                try {
                    String categoryPatternThatTopic = "";
                    if (node.shortCut) {
                        //System.out.println("Partial patternThatTopic = "+partialPatternThatTopic);
                        categoryPatternThatTopic = partialPatternThatTopic + " <THAT> * <TOPIC> *";
                    }
                    else categoryPatternThatTopic = partialPatternThatTopic;
                    Category c = new Category(0, categoryPatternThatTopic,  MagicStrings.blank_template, MagicStrings.unknown_aiml_file);
                    //if (brain.existsCategory(c)) System.out.println(c.inputThatTopic()+" Exists");
                    //if (deleted.existsCategory(c)) System.out.println(c.inputThatTopic()+ " Deleted");
                    if (!bot.brain.existsCategory(c) && !deletedGraph.existsCategory(c)/* && !unfinishedGraph.existsCategory(c)*/) {
                        patternGraph.addCategory(c);
                        suggestedCategories.add(c);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(NodemapperOperator.size(node) > MagicNumbers.node_size) {
            //System.out.println("STAR: "+NodemapperOperator.size(node)+". "+partialPatternThatTopic+" * <that> * <topic> *");
            starPatternCnt ++;
            try {
                Category c = new Category(0, partialPatternThatTopic+" * <THAT> * <TOPIC> *",  MagicStrings.blank_template, MagicStrings.unknown_aiml_file);
                //if (brain.existsCategory(c)) System.out.println(c.inputThatTopic()+" Exists");
                //if (deleted.existsCategory(c)) System.out.println(c.inputThatTopic()+ " Deleted");
                if (!bot.brain.existsCategory(c) && !deletedGraph.existsCategory(c)/* && !unfinishedGraph.existsCategory(c)*/) {
                    patternGraph.addCategory(c);
                    suggestedCategories.add(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (String key : NodemapperOperator.keySet(node)) {
            Nodemapper value = NodemapperOperator.get(node, key);
            findPatterns(value, partialPatternThatTopic + " " + key);
        }

    }

    /** classify inputs into matching categories
     *
     * @param filename    file containing sample normalized inputs
     */

    public void classifyInputs (String filename) {
        try{
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            //Read File Line By Line
            int count = 0;
            while ((strLine = br.readLine())!= null && count < limit)   {
                // Print the content on the console
                //System.out.println("Classifying "+strLine);

                if (strLine != null) {
                    if (strLine.startsWith("Human: ")) strLine = strLine.substring("Human: ".length(), strLine.length());
                    String sentences[] = bot.preProcessor.sentenceSplit(strLine);
                    for (int i = 0; i < sentences.length; i++) {
                        String sentence = sentences[i];
                        if (sentence.length() > 0) {
                            Nodemapper match = patternGraph.match(sentence, "unknown", "unknown");

                            if (match == null) {
                                System.out.println(sentence+" null match");
                            }
                            else {
                                match.category.incrementActivationCnt();
                                //System.out.println(count+". "+sentence+" matched "+match.category.inputThatTopic());
                            }
                            count += 1;
                            if (count%10000 == 0) System.out.println(count);
                        }
                    }
                }
            }
            System.out.println("Finished classifying "+count+" inputs");
            //Close the input stream
            br.close();
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**  magically suggests new patterns for a bot.
        * Reads an input file of sample data called logFile.
        * Builds a graph of all the inputs.
        * Finds new patterns in the graph that are not already in the bot.
        * Classifies input log into those new patterns.
        *
        *
        *
        */
    public void ab () {
        String logFile = logfile;
        MagicBooleans.trace_mode = false;
        MagicBooleans.enable_external_sets = false;
        if (offer_alice_responses) alice = new Bot("alice");
        Timer timer = new Timer();
        bot.brain.nodeStats();
        if (bot.brain.getCategories().size() < MagicNumbers.brain_print_size) bot.brain.printgraph();
        timer.start();
        System.out.println("Graphing inputs");
        graphInputs(logFile);
        System.out.println(timer.elapsedTimeSecs() + " seconds Graphing inputs");
        inputGraph.nodeStats();
        if (inputGraph.getCategories().size() < MagicNumbers.brain_print_size) inputGraph.printgraph();
        //bot.inputGraph.printgraph();
        timer.start();
        System.out.println("Finding Patterns");
        findPatterns();
        System.out.println(suggestedCategories.size()+" suggested categories");
        System.out.println(timer.elapsedTimeSecs() + " seconds finding patterns");
        timer.start();
        patternGraph.nodeStats();
        if (patternGraph.getCategories().size() < MagicNumbers.brain_print_size) patternGraph.printgraph();
        System.out.println("Classifying Inputs from "+logFile);
        classifyInputs(logFile);
        System.out.println(timer.elapsedTimeSecs() + " classifying inputs");
    }

    public ArrayList<Category> nonZeroActivationCount(ArrayList<Category> suggestedCategories) {
        ArrayList<Category> result = new ArrayList<Category>();
        for (Category c : suggestedCategories) {
            if (c.getActivationCnt() > 0) result.add(c);
           // else     System.out.println("["+c.getActivationCnt()+"] "+c.inputThatTopic());
        }

        return result;
    }
       /** train the bot through a terminal interaction
        *
        */
    public void terminalInteraction () {
        boolean firstInteraction = true;
        String alicetemplate = null;
        Timer timer = new Timer();
        sort_mode = !shuffle_mode;
       // if (sort_mode)
        Collections.sort(suggestedCategories, Category.ACTIVATION_COMPARATOR);
        ArrayList<Category> topSuggestCategories = new ArrayList<Category>();
        for (int i = 0; i < 10000 && i < suggestedCategories.size(); i++) {
            topSuggestCategories.add(suggestedCategories.get(i));
        }
        suggestedCategories = topSuggestCategories;
        if (shuffle_mode) Collections.shuffle(suggestedCategories);
        timer = new Timer();
        timer.start();
        runCompletedCnt = 0;
        ArrayList<Category> filteredAtomicCategories = new ArrayList<Category>();
        ArrayList<Category> filteredWildCategories = new ArrayList<Category>();
        for (Category c : suggestedCategories) if (!c.getPattern().contains("*")) filteredAtomicCategories.add(c);
        else filteredWildCategories.add(c);
        ArrayList <Category> browserCategories;
        if (filter_atomic_mode) browserCategories = filteredAtomicCategories;
        else if (filter_wild_mode) browserCategories = filteredWildCategories;
        else browserCategories = suggestedCategories;
        // System.out.println(filteredAtomicCategories.size()+" filtered suggested categories");
        browserCategories = nonZeroActivationCount(browserCategories);
        for (Category c : browserCategories)  {
            try {
            ArrayList samples = new ArrayList(c.getMatches(bot));
            Collections.shuffle(samples);
            int sampleSize = Math.min(MagicNumbers.displayed_input_sample_size, c.getMatches(bot).size());
            for (int i = 0; i < sampleSize; i++) {
                System.out.println("" + samples.get(i));
            }
            System.out.println("["+c.getActivationCnt()+"] "+c.inputThatTopic());
            Nodemapper node;
                if (offer_alice_responses) {
                    node = alice.brain.findNode(c);
                    if (node != null) {
                        alicetemplate = node.category.getTemplate();
                        String displayAliceTemplate = alicetemplate;
                        displayAliceTemplate = displayAliceTemplate.replace("\n", " ");
                        if (displayAliceTemplate.length() > 200) displayAliceTemplate = displayAliceTemplate.substring(0, 200);
                        System.out.println("ALICE: "+displayAliceTemplate);
                    }
                    else alicetemplate = null;
                }

            String textLine = "" + IOUtils.readInputTextLine();
            if (firstInteraction) { timer.start(); firstInteraction = false;}
            productivity(runCompletedCnt, timer);
            terminalInteractionStep(bot, "", textLine, c, alicetemplate);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Returning to Category Browser");
            }
        }
        System.out.println("No more samples");
        bot.writeAIMLFiles();
        bot.writeAIMLIFFiles();
    }

       /** process one step of the terminal interaction
        *
        * @param bot     the bot being trained.
        * @param request      used when this routine is called by benchmark testSuite
        * @param textLine     response typed by the botmaster
        * @param c            AIML category selected
        */
   public void terminalInteractionStep (Bot bot, String request, String textLine, Category c, String alicetemplate) {
       String template = null;
       if (textLine.contains("<pattern>") && textLine.contains("</pattern>")) {
           int index = textLine.indexOf("<pattern>")+"<pattern>".length();
           int jndex = textLine.indexOf("</pattern>");
           int kndex = jndex + "</pattern>".length();
           if (index < jndex) {
               String pattern = textLine.substring(index, jndex);
               c.setPattern(pattern);
               textLine = textLine.substring(kndex, textLine.length());
               System.out.println("Got pattern = "+pattern+" template = "+textLine);
           }
       }
       String botThinks = "";
       String[] pronouns = {"he", "she", "it", "we", "they"};
       for (String p : pronouns) {
           if (textLine.contains("<"+p+">")) {
               textLine = textLine.replace("<"+p+">","");
               botThinks = "<think><set name=\""+p+"\"><set name=\"topic\"><star/></set></set></think>";
           }
       }
       if (textLine.equals("q")) System.exit(0);       // Quit program
       else if (textLine.equals("wq")) {   // Write AIML Files and quit program
           bot.writeQuit();
         /*  Nodemapper udcNode = bot.brain.findNode("*", "*", "*");
           if (udcNode != null) {
               AIMLSet udcMatches = new AIMLSet("udcmatches");
               udcMatches.addAll(udcNode.category.getMatches());
               udcMatches.writeAIMLSet();
           }*/
          /* Nodemapper cNode = bot.brain.match("JOE MAKES BEER", "unknown", "unknown");
           if (cNode != null) {
               AIMLSet cMatches = new AIMLSet("cmatches");
               cMatches.addAll(cNode.category.getMatches());
               cMatches.writeAIMLSet();
           }
           if (passed.size() > 0) {
               AIMLSet difference = new AIMLSet("difference");
               AIMLSet intersection = new AIMLSet("intersection");
               for (String s : passed) if (testSet.contains(s)) intersection.add(s);
               passed = intersection;
               passed.setName = "passed";
               difference.addAll(testSet);
               difference.removeAll(passed);
               difference.writeAIMLSet();

               passed.writeAIMLSet();
               testSet.writeAIMLSet();
               System.out.println("Wrote passed test cases");
           }*/
           System.exit(0);
       }
       else if (textLine.equals("skip") || textLine.equals("")) { // skip this one for now
           skipCategory(c);
       }
       else if (textLine.equals("s") || textLine.equals("pass")) { //
           passed.add(request);
           AIMLSet difference = new AIMLSet("difference", bot);
           difference.addAll(testSet);
           difference.removeAll(passed);
           difference.writeAIMLSet();
           passed.writeAIMLSet();
       }
       else if (textLine.equals("a")) {
           template = alicetemplate;
           String filename;
           if (template.contains("<sr")) filename = MagicStrings.reductions_update_aiml_file;
           else filename = MagicStrings.personality_aiml_file;
           saveCategory(c.getPattern(), template, filename);
       }
       else if (textLine.equals("d")) { // delete this suggested category
           deleteCategory(c);
       }
       else if (textLine.equals("x")) {    // ask another bot
           template = "<sraix services=\"pannous\">"+c.getPattern().replace("*","<star/>")+"</sraix>";
           template += botThinks;
           saveCategory(c.getPattern(), template, MagicStrings.sraix_aiml_file);
       }
       else if (textLine.equals("p")) {   // filter inappropriate content
           template = "<srai>"+MagicStrings.inappropriate_filter+"</srai>";
           template += botThinks;
           saveCategory(c.getPattern(), template, MagicStrings.inappropriate_aiml_file);
       }
       else if (textLine.equals("f")) { // filter profanity
           template = "<srai>"+MagicStrings.profanity_filter+"</srai>";
           template += botThinks;
           saveCategory(c.getPattern(), template, MagicStrings.profanity_aiml_file);
       }
       else if (textLine.equals("i")) {
           template = "<srai>"+MagicStrings.insult_filter+"</srai>";
           template += botThinks;
           saveCategory(c.getPattern(), template, MagicStrings.insult_aiml_file);
       }
       else if (textLine.contains("<srai>") ||  textLine.contains("<sr/>"))  {
           template = textLine;
           template += botThinks;
           saveCategory(c.getPattern(), template, MagicStrings.reductions_update_aiml_file);
       }
       else if (textLine.contains("<oob>"))  {
           template = textLine;
           template += botThinks;
           saveCategory(c.getPattern(), template, MagicStrings.oob_aiml_file);
       }
       else if (textLine.contains("<set name") || botThinks.length() > 0) {
           template = textLine;
           template += botThinks;
           saveCategory(c.getPattern(), template, MagicStrings.predicates_aiml_file);
       }
       else if (textLine.contains("<get name") && !textLine.contains("<get name=\"name")) {
           template = textLine;
           template += botThinks;
           saveCategory(c.getPattern(), template, MagicStrings.predicates_aiml_file);
       }
       else {
           template = textLine;
           template += botThinks;
           saveCategory(c.getPattern(), template, MagicStrings.personality_aiml_file);
       }

   }

}

