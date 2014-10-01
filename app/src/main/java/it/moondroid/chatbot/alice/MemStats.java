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

import it.moondroid.chatbot.alice.utils.MemoryUtils;

/**
 * Memory statistics for program instrumentation
 *
 */
public class MemStats {
    public static long prevHeapSize = 0;

    /**
     * print out some statistics about heap size
     */
    public static void memStats ()  {
    // Get current size of heap in bytes
    long heapSize = MemoryUtils.totalMemory();

    // Get maximum size of heap in bytes. The heap cannot grow beyond this size.
// Any attempt will result in an OutOfMemoryException.
    long heapMaxSize = MemoryUtils.maxMemory();

    // Get amount of free memory within the heap in bytes. This size will increase
// after garbage collection and decrease as new objects are created.
    long heapFreeSize = MemoryUtils.freeMemory();
    long diff = heapSize - prevHeapSize;
    prevHeapSize = heapSize;
    System.out.println("Heap "+heapSize+" MaxSize "+heapMaxSize+" Free "+heapFreeSize+" Diff "+diff);

    }

}
