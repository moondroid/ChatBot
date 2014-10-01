package it.moondroid.chatbot.alice;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is here to simulate a Contacts database for the purpose of testing contactaction.aiml
 */
public class Contact {
    public static int contactCount=0;
    public static HashMap<String, Contact> idContactMap = new HashMap<String, Contact>();
    public static HashMap<String, String> nameIdMap = new HashMap<String, String>();
    public String contactId;
    public String displayName;
    public String birthday;
    public HashMap<String, String> phones;
    public HashMap<String, String> emails;
    public static String multipleIds(String contactName) {
        String patternString = " ("+contactName.toUpperCase()+") ";
        while (patternString.contains(" ")) patternString = patternString.replace(" ", "(.*)");
        //System.out.println("Pattern='"+patternString+"'");
        Pattern pattern = Pattern.compile(patternString);
        Set<String> keys = nameIdMap.keySet();
        String result="";
        int idCount = 0;
        for (String key : keys) {
            Matcher m = pattern.matcher(key);
            if (m.find()) {
                result += nameIdMap.get(key.toUpperCase())+" ";
                idCount++;
            }
        }
        if (idCount <= 1) result = "false";
        return result.trim();
    }
    public static String contactId(String contactName) {
        String patternString = " "+contactName.toUpperCase()+" ";
        while (patternString.contains(" ")) patternString = patternString.replace(" ", ".*");
        //System.out.println("Pattern='"+patternString+"'");
        Pattern pattern = Pattern.compile(patternString);
        Set<String> keys = nameIdMap.keySet();
        String result="unknown";
        for (String key: keys) {
            Matcher m = pattern.matcher(key);
            if (m.find()) result = nameIdMap.get(key.toUpperCase())+" ";
        }
        return result.trim();
    }
    public static String displayName(String id) {
        Contact c = idContactMap.get(id.toUpperCase());
        String result = "unknown";
        if (c != null) {
            result = c.displayName;
        }
        return result;
    }
    public static String dialNumber(String type, String id) {
        String result = "unknown";
        Contact c = idContactMap.get(id.toUpperCase());
        if (c != null) {
            String dialNumber = c.phones.get(type.toUpperCase());
            if (dialNumber != null) result = dialNumber;
        }
        return result;
    }
    public static String emailAddress(String type, String id) {
        String result = "unknown";
        Contact c = idContactMap.get(id.toUpperCase());
        if (c != null) {
            String emailAddress = c.emails.get(type.toUpperCase());
            if (emailAddress != null) result = emailAddress;
        }
        return result;
    }
    public static String birthday(String id) {
        Contact c = idContactMap.get(id.toUpperCase());
        if (c == null) return "unknown";
        else return c.birthday;
    }
    public Contact (String displayName, String phoneType, String dialNumber, String emailType, String emailAddress, String birthday) {
        contactId = "ID"+contactCount;
        contactCount++;
        phones = new HashMap<String, String>();
        emails = new HashMap<String, String>();
        idContactMap.put(contactId.toUpperCase(), this);
        addPhone(phoneType, dialNumber);
        addEmail(emailType, emailAddress);
        addName(displayName);
        addBirthday(birthday);
    }

    public void addPhone(String type, String dialNumber) {
        phones.put(type.toUpperCase(), dialNumber);
    }
    public void addEmail(String type, String emailAddress) {
        emails.put(type.toUpperCase(), emailAddress);
    }
    public void addName (String name) {
        displayName = name;
        nameIdMap.put(displayName.toUpperCase(), contactId);
        //System.out.println(nameIdMap.toString());
    }
    public void addBirthday(String birthday) {
        this.birthday = birthday;
    }


}
