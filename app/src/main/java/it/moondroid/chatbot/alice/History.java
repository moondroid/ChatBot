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

/**
 * History object to maintain history of input, that request and response
 *
 * @param <T>    type of history object
 */
public class History<T> {
    private Object[] history;
    private String name;

    /**
     * Constructor with default history name
     */
    public History () {
        this("unknown");
    }

    /**
     * Constructor with history name
     *
     * @param name    name of history
     */
    public History(String name) {
        this.name = name;
        history = new Object[MagicNumbers.max_history];
    }

    /**
     * add an item to history
     *
     * @param item    history item to add
     */
    public void add(T item) {
        for (int i = MagicNumbers.max_history-1; i > 0; i--) {
              history[i] = history[i-1];
        }
        history[0] = item;
    }

    /**
     * get an item from history
     *
     * @param index       history index
     * @return            history item
     */
    public T get (int index) {
        if (index < MagicNumbers.max_history) {
            if (history[index] == null) return null;
            else return (T)history[index];
        }
        else return null;
    }

    /**
     * get a String history item
     *
     * @param index    history index
     * @return         history item
     */
    public String getString (int index) {
        if (index < MagicNumbers.max_history) {
            if (history[index] == null) return MagicStrings.unknown_history_item;
            else return (String)history[index];
        }
        else return null;
    }

    /**
     * print history
     */
    public void printHistory() {
        int i;
        for (i = 0; get(i) != null; i++) {
            System.out.println(name+"History "+(i+1)+" = "+get(i));
            System.out.println(String.valueOf(get(i).getClass()).contains("History"));
            if (String.valueOf(get(i).getClass()).contains("History")) ((History)get(i)).printHistory();
        }
    }
}
