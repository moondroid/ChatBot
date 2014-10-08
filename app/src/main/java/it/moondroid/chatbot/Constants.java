package it.moondroid.chatbot;

/**
 * Created by marco.granatiero on 03/10/2014.
 */
public final class Constants {

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION_BRAIN_STATUS =
            "it.moondroid.chatbot.BROADCAST_ACTION_BRAIN_STATUS";
    public static final String BROADCAST_ACTION_BRAIN_ANSWER =
            "it.moondroid.chatbot.BROADCAST_ACTION_BRAIN_ANSWER";
    public static final String BROADCAST_ACTION_LOGGER =
            "it.moondroid.chatbot.BROADCAST_ACTION_LOGGER";

    // Defines the key for the status "extra" in an Intent
    public static final String EXTRA_BRAIN_STATUS =
            "it.moondroid.chatbot.BRAIN_STATUS";
    public static final String EXTRA_BRAIN_ANSWER =
            "it.moondroid.chatbot.EXTRA_BRAIN_ANSWER";

    public static final int STATUS_BRAIN_LOADING = -1;
    public static final int STATUS_BRAIN_LOADED = 1;

    public static final String EXTENDED_LOGGER_INFO =
            "it.moondroid.chatbot.LOGGER_INFO";

}
