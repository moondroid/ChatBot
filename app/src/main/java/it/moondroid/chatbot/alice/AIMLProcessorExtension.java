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

import org.w3c.dom.Node;

import java.util.Set;

/**
 * The interface needed to implement AIML Extension
 *
 * A class implementing AIMLProcessorExtension should return
 * a Set of tag names and provide a function to recursively evaluate the
 * XML parse tree for each node associated with a new tag.
 */
public interface AIMLProcessorExtension {
    /**
     * provide the AIMLProcessor with a list of extension tag names.
     *
     * @return      Set of extension tag names
     */
   public Set<String> extensionTagSet();

    /**
     * recursively evaluate AIML from a node corresponding an extension tag
     *
     * @param node                current XML parse node
     * @param ps                  current parse state
     * @return                    result of evaluating AIML
     */
   public String recursEval(Node node, ParseState ps);
}
