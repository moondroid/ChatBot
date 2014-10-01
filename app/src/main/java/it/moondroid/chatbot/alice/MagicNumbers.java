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
 * Integers with specific values in Program AB
 *
 */
public class MagicNumbers {
    public static int node_activation_cnt = 4;  // minimum number of activations to suggest atomic pattern
    public static int node_size = 4;  // minimum number of branches to suggest wildcard pattern
    public static int displayed_input_sample_size = 6;
    public static int max_history = 32;
    public static int repetition_count = 2;
    public static int max_stars = 1000;
    public static int max_graph_height = 100000;
    public static int max_substitutions = 10000;
    public static int max_recursion_depth = 765; // assuming java -Xmx512M
    public static int max_recursion_count = 2048;
    public static int max_trace_length = 2048;
    public static int max_loops = 10000;
    public static int estimated_brain_size = 5000;
    public static int max_natural_number_digits = 10000;
    public static int brain_print_size = 100; // largest size of brain to print to System.out
}
