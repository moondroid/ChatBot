package it.moondroid.chatbot.alice;
/* Program AB Reference AIML 2.1 implementation
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


import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import it.moondroid.chatbot.BrainLogger;


public class Alice {

    private static Context context;
    private static Bot bot;
    private static Alice instance;

    public static Alice setup(Context context){
        instance = new Alice(context, new String[0]);
        return instance;
    }

    public static Alice getInstance(){
        if (instance==null){
            throw new UnsupportedOperationException("You forgot to call setup method to inizialize Alice brain");
        }
        return instance;
    }

    private Alice (Context context, String[] args) {
        this.context = context;
        MagicStrings.setRootPath();

        AIMLProcessor.extension =  new PCAIMLProcessorExtension();
        mainFunction(args);
    }

    public static Context getContext(){
        return context;
    }

    public static void mainFunction (String[] args) {
        String botName = "alice-it";
        MagicBooleans.jp_tokenize = false;
        MagicBooleans.trace_mode = true;
        String action="chat";

        if (MagicBooleans.trace_mode){
            System.out.println(MagicStrings.program_name_version);
            BrainLogger.getInstance().info(MagicStrings.program_name_version);
        }

        for (String s : args) {
            //System.out.println(s);
            String[] splitArg = s.split("=");
            if (splitArg.length >= 2) {
                String option = splitArg[0];
                String value = splitArg[1];
                //if (MagicBooleans.trace_mode) System.out.println(option+"='"+value+"'");
                if (option.equals("bot")) botName = value;
                if (option.equals("action")) action = value;
                if (option.equals("trace")) {
                    if (value.equals("true")) MagicBooleans.trace_mode = true;
                    else MagicBooleans.trace_mode = false;
                }
                if (option.equals("morph")) {
                    if (value.equals("true")) MagicBooleans.jp_tokenize = true;
                    else {
                        MagicBooleans.jp_tokenize = false;
                    }
                }
             }
        }
        if (MagicBooleans.trace_mode) System.out.println("Working Directory = " + MagicStrings.root_path);
        Graphmaster.enableShortCuts = true;
        //Timer timer = new Timer();
        bot = new Bot(botName, MagicStrings.root_path, action); //
        //EnglishNumberToWords.makeSetMap(bot);
        //getGloss(bot, "c:/ab/data/wn30-lfs/wne-2006-12-06.xml");
        if (MagicBooleans.make_verbs_sets_maps) Verbs.makeVerbSetsMaps(bot);
        //bot.preProcessor.normalizeFile("c:/ab/data/log2.txt", "c:/ab/data/log2normal.txt");
        //System.exit(0);
        if (bot.brain.getCategories().size() < MagicNumbers.brain_print_size) bot.brain.printgraph();
        if (MagicBooleans.trace_mode) System.out.println("Action = '"+action+"'");
//        if (action.equals("chat") || action.equals("chat-app")) {
//			boolean doWrites = ! action.equals("chat-app");
//			TestAB.testChat(bot, doWrites, MagicBooleans.trace_mode);
//		}
        //else if (action.equals("test")) testSuite(bot, MagicStrings.root_path+"/data/find.txt");
        else if (action.equals("ab")) TestAB.testAB(bot, TestAB.sample_file);
        else if (action.equals("aiml2csv") || action.equals("csv2aiml")) convert(bot, action);
        else if (action.equals("abwq")){AB ab = new AB(bot, TestAB.sample_file);  ab.abwq();}
		else if (action.equals("test")) { TestAB.runTests(bot, MagicBooleans.trace_mode);     }
        else if (action.equals("shadow")) { MagicBooleans.trace_mode = false; bot.shadowChecker();}
        else if (action.equals("iqtest")) { ChatTest ct = new ChatTest(bot);
                try {
                    ct.testMultisentenceRespond();
                }
            catch (Exception ex) { ex.printStackTrace(); }
            }
        else System.out.println("Unrecognized action "+action);
    }

    public static void convert(Bot bot, String action) {
        if (action.equals("aiml2csv")) bot.writeAIMLIFFiles();
        else if (action.equals("csv2aiml")) bot.writeAIMLFiles();
    }

    public String processInput(String request) {
        String response = "";
        Chat chatSession = new Chat(bot, false);
        bot.brain.nodeStats();
        response = chatSession.multisentenceRespond(request);
        while (response.contains("&lt;")) response = response.replace("&lt;","<");
        while (response.contains("&gt;")) response = response.replace("&gt;",">");

        return response;
    }

    public static void getGloss (Bot bot, String filename) {
        System.out.println("getGloss");
        try{
            // Open the file that is the first
            // command line parameter
            File file = new File(filename);
            if (file.exists()) {
                FileInputStream fstream = new FileInputStream(filename);
                // Get the object
                getGlossFromInputStream(bot, fstream);
                fstream.close();
            }
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    public static void getGlossFromInputStream (Bot bot, InputStream in)  {
        System.out.println("getGlossFromInputStream");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        int cnt = 0;
        int filecnt = 0;
        HashMap<String, String> def = new HashMap<String, String>();
        try {
            //Read File Line By Line
            String word; String gloss;
            word = null;
            gloss = null;
            while ((strLine = br.readLine()) != null)   {

                if (strLine.contains("<entry word")) {
                    int start = strLine.indexOf("<entry word=\"")+"<entry word=\"".length();
                    //int end = strLine.indexOf(" status=");
                    int end = strLine.indexOf("#");

                    word = strLine.substring(start, end);
                    word = word.replaceAll("_"," ");
                    System.out.println(word);

                }
                else  if (strLine.contains("<gloss>")) {
                    gloss = strLine.replaceAll("<gloss>","");
                    gloss = gloss.replaceAll("</gloss>","");
                    gloss = gloss.trim();
                    System.out.println(gloss);

                }


                if (word != null && gloss != null) {
                    word = word.toLowerCase().trim();
                    if (gloss.length() > 2) gloss = gloss.substring(0, 1).toUpperCase()+gloss.substring(1, gloss.length());
                    String definition;
                    if (def.keySet().contains(word))  {
                        definition = def.get(word);
                        definition = definition+"; "+gloss;
                    }
                    else definition = gloss;
                    def.put(word, definition);
                    word = null;
                    gloss = null;
                }
            }
            Category d = new Category(0,"WNDEF *","*","*","unknown","wndefs"+filecnt+".aiml");
            bot.brain.addCategory(d);
            for (String x : def.keySet()) {
                word = x;
                gloss = def.get(word)+".";
                cnt++;
                if (cnt%5000==0) filecnt++;

                Category c = new Category(0,"WNDEF "+word,"*","*",gloss,"wndefs"+filecnt+".aiml");
                System.out.println(cnt+" "+filecnt+" "+c.inputThatTopic()+":"+c.getTemplate()+":"+c.getFilename());
                Nodemapper node;
                if ((node = bot.brain.findNode(c)) != null) node.category.setTemplate(node.category.getTemplate()+","+gloss);
                bot.brain.addCategory(c);


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sraixCache (String filename, Chat chatSession) {
        int limit = 1000;
        try {
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            //Read File Line By Line
            int count = 0;
            while ((strLine = br.readLine()) != null && count++ < limit) {
                System.out.println("Human: " + strLine);

                String response = chatSession.multisentenceRespond(strLine);
                System.out.println("Robot: " + response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
