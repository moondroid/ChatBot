package it.moondroid.chatbot.alice.utils;


public class MemoryUtils {


    public static long totalMemory() {
		return Runtime.getRuntime().totalMemory();
	}


    public static long maxMemory() {
		return Runtime.getRuntime().maxMemory();
	}


    public static long freeMemory() {
		return Runtime.getRuntime().freeMemory();
	}
}
