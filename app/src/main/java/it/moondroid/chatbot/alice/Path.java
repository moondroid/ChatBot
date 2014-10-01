package it.moondroid.chatbot.alice;
import java.util.ArrayList;
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

/**
 * Linked list representation of Pattern Path and Input Path
 */
public class Path extends ArrayList<String>{
    public String word;
    public Path next;
    public int length;

    /**
     * Constructor - class has public members
     */
    private Path() {
        next = null;
        word = null;
        length = 0;
    }

    /**
     * convert a sentence (a string consisting of words separated by single spaces) into a Path
     *
     * @param sentence        sentence to convert
     * @return                sentence in Path form
     */
    public static Path sentenceToPath(String sentence) {
        sentence = sentence.trim();
        return arrayToPath(sentence.split(" "));
    }

    /**
     * The inverse of sentenceToPath
     *
     * @param path           input path
     * @return               sentence
     */
    public static String pathToSentence (Path path) {
        String result="";
        for (Path p = path; p != null; p = p.next) {
            result = result+" "+p.word;
        }
        return result.trim();
       /* if (path == null) return "";
        else return path.word+" "+pathToSentence(path.next);*/
    }

    /**
     * convert an array of strings to a Path
     *
     * @param array     array of strings
     * @return          sequence of strings as Path
     */
    private static Path arrayToPath(String[] array) {
        Path tail = null;
        Path head = null;
        for (int i = array.length-1; i >= 0; i--) {
            head = new Path();
            head.word = array[i];
            head.next = tail;
            if (tail == null) head.length = 1;
            else head.length = tail.length + 1;
            tail = head;
        }
        return head;
        //return arrayToPath(array, 0);
    }

    /**
     * recursively convert an array to a Path
     *
     * @param array  array of strings
     * @param index  array index
     * @return       Path form
     */
    private static Path arrayToPath(String[] array, int index)  {
        if (index >= array.length) return null;
        else {
            Path newPath = new Path();
            newPath.word = array[index];
            newPath.next = arrayToPath(array, index+1);
            if (newPath.next == null) newPath.length = 1;
            else newPath.length = newPath.next.length + 1;
            return newPath;
        }
    }

    /**
     * print a Path
     */
    public void print() {
        String result = "";
        for (Path p = this; p != null; p = p.next) {
            result += p.word+",";
        }
        if (result.endsWith(",")) result = result.substring(0, result.length()-1);
        System.out.println(result);
    }

}
