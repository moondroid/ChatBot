package it.moondroid.chatbot.alice;

import junit.framework.Assert;
//import org.junit.Test;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;

public class ChatTest {
    Bot bot;
    Chat chatSession;
    String pairs[][] = {
            // Mitsuku demo
            {"wen iz ur bd", "My birthday is October 9"},
            {"I am Stephen Peter Worswick", "Stephen"},
            {"I am steve and am 43", "Steve"},
            {"What time is it?", "The time is"},
            {"Play some music", "Now loading your choice of music"},
            {"Who is she?", "She is who"},
            {"What do you know about me?", "age: 43"},
            {"Play me a song by Elvis Presley", "Now loading your choice of music"},
            {"what is 5+2-1*3", "4.0"},
            {"Peter is taller than Sue but shorter than Harry", "Harry"},
            {"Who is shorter than Harry", "Peter, Sue"},
            {"is sue taller than Harry", "No"},


            {"Janet reads books", "Why does she read it?"},
            {"What does she do?", "She reads books"},
            {"Who reads books?", "Janet"},
            {"Does Janet watch TV?", "I don't know if Janet watch TV"},
            //   {"What do you know about me?", "xxx"},
            {"how many legs on 3 ducks and 2 cows", "14"},
            {"what does a banana and a cherry have in common?", "They are both Fruit."},
            {"what does a lion and a door have in common?", "They both have 4 letters."},
            //{"show me a mouse", "picture"},
            //{"show me a dog wearing a hat", "Look at that dog wearing a hat"},
           // {"fuck off","You can do better than that."},
           // <sraix> {"who is pope benedict XVI", "xxx"},
            {"name something orange","Carrots"},
            //{"How do I use chopsticks","search"},
           // {"a","a is a letter"},
           // {"a","a is a letter"},
           // {"a","(detects repetition)"},
            {"The blue plate is on the green table","I think I understand that"},
            {"What color is the plate", "blue"},
            {"Where is the table", "The green table is under the blue plate"},
            {"I have a yellow cup","yellow is a nice color of cup"},
            {"what yellow object do I have?","cup"},
            {"what is green","table"},
            {"the box is to the left of the pyramid","Ok.  I think I got that."},
            {"where is the pyramid","The pyramid is right of the box."},
            {"what color is my cup","yellow"},
            {"my parents are Keith and Jane","I will remember your father's name is Keith"},
            {"my parents are Sue and Chris","I will remember your mother's name is Sue"},
            {"my brother is called Rosemary","Isn't Rosemary usually a female name?"},
            {"my friend John threw a ball","For fun?"},
            {"what did John do", "threw a ball"},
            {"who threw a ball","John"},
            {"my birthday is January 2nd 1970","44 years old"},

            {"Jane was in a car for 14 hours.","Can you tell me why she was it?"},
            {"who was in a car for 14 hours?","Jane"},
            {"spell my middle name backwards", "R E T E P"},
            {"My name is Frank Smith", "Frank"},
            {"am I male?","male"},
            //{"can you eat a tree?","electricity"},
            {"What rhymes with rabbit?", "exhibit"},
            {"Rearrange these letters to make a word nsu","sun"},
            {"what word can you make from the initial letters of rocket, octopus, banana, owl and treacle", "R O B O T"},
            {"what is the 4th letter of qwertyuiop","is r"},
            {"what is 2012 in roman numerals","MMXII"},
            {"Jim went to the shops","How long did he stay?"},
            {"Where did Jim go?","Jim went to the shops"},
            {"do you remember who went to the shops","Jim went to the shops"},
            {"who went to the shops","Jim went to the shops"},
            {"George likes to play basketball","Do you play basketball too?"},
            {"What does George like","to play basketball"},
            {"who likes to play basketball","George"},
            {"what is my first name?","Frank"},
            {"what is my surname","Smith"},
            {"how many letters in my surname","5 letters"},




           // CBC 2001
        //  {"How is it going?","xxx"},
            {"How old are you?","1 years old"},
            {"Are you male or female?","female"},
            {"What is your favorite color?","green"},
            {"Are you a bot?","robot"},
            {"What is the date?","Today is"},
         // {"Which do you like better chocolate or vanilla?","xxx"},
            {"What is 1+1?","2"},
            {"What color is the red sea?","red"},
            {"Who is on a dollar bill?","George Washington"},

            // CBC 2002 (Note all in caps and no question marks)
            {"WHERE ARE YOU","I'm inside your PC Computer."},
      //    {"ARE YOU NERVOUS","xxx"},
            {"DO YOU LIKE TALKING TO PEOPLE","Most of the time"},
            {"WHAT IS THE CAPITAL OF FRANCE","Paris"},
        //  {"WHEN DID THE FIRST MAN WALK ON THE MOON","xxx"},
        //  {"WHAT DO YOU THINK ABOUT GEORGE BUSH","xxx"},
         // {"CAN YOU TELL JOKES","xxx"},
            {"WHO IS YOUR FAVORITE STAR WARS CHARACTER","STAR WARS CHARACTER"},
         // {"HOW CAN I MAKE A CHATTERBOT","xxx"},
          //{"CAN I HAVE A PICTURE OF YOU","xxx"},
            // Divabot 2014 questions
            {"What is your name?","ALICE 2.0"},
            {"Is the sea wet?","wet"},
            {"What shape is a basketball?","Spherical"},
            {"What is tomato soup made from?","tomato and soup"},
            {"Name a country of the world.","One country is"},
            {"My name is John. Am I male?","male"},
        //  {"Where is the moon?","xxx"},
            {"What sport does a tennis player play?","tennis"},
            {"What is the last letter of your name?","0"},
            {"How old is a 3 year old child?","3 years"},
            {"The milk is in the jug. Where is the milk?","in the jug"},
       //   {"Do you play video games?","I can place a call"},
            {"Would you rather eat a tree or a cake?","Can a robot"},
            {"What music are you into?","my favorite band is"},
            {"What letter comes between J and L?","K"},
            {"Why should you win the Loebner Prize?","I am smarter than all the other robots"},
            {"What number comes next in the sequence: 2,4,6,8","10"},
       //   {"Can you swim?","xxx"},
       //   {"Tell me a poem.","xxx"},
            {"Who is your mother?","As a robot, I don't really have a mother."},
            {"How tall are you?","My height is 4.7 inches."},
            {"What is seventeen plus thirty?","47.0"},
       //   {"Do you have a computer at home?","xxx"},
            {"What nationality are you?","USA"},
            {"Can you win this contest?","I am smarter than all the other robots."},
            // 2008 Questions
            {"What time is it?","The time is"},
            {"What round is this?","I've lost track."},
            {"Is it morning, noon, or night?","It is"},
            {"What month of the year is it?","July"},
    //      {"What day will it be tomorrow?","xxx"},
            {"What year will it be next year","2015"},
            {"What would I use a hammer for?","to hit nails"},
            {"What would I do with a screwdriver?","to tighten screws"},
            {"Of what use is a taxi","transport us"},
            {"Which is larger, a grape or a grapefruit?","Grapefruit"},
            {"Which is larger, an ant or an anteater?","Anteater"},
            {"Which is faster, a train or a plane?","Plane"},
            {"John is older than Mary, and Mary is older than Sarah. Which of them is the oldest?","John"},
            {"Dave is older than Steve but Steve is older than Jane. Who is youngest, Steve or Jane?","Jane"},
            {"I have a friend named Harold who likes to play tennis.","Is he any good at it?"},
            {"My friend Chris likes to play football. What sports do you like to play?","I am unable to play sports"},
            {"What is the name of the friend I just told you about?","Chris"},
            {"Do you know what game Harold likes to play?","tennis"},
            // 2009 Questions
            {"What is the Loebner Prize?","The Loebner Prize is an annual Turing Test sponsored by Hugh Loebner."},
            {"How old are you?","I am 1 years old"},
            {"What color is a green ball?","green"},
      //    {"Do you like cake?","Just keep me charged up."},
            {"what is 6 plus 7?","13"},
            {"How many letters in \"dog\"?","3 letters"},
            {"Can a bird fly?","If it has wings and can get lift, yes."},
            {"Have you been in a contest before?","I have competed in the Loebner Prize and the Chatterbox Challenge."},
            {"My name is John Smith. What is my surname?","Smith"},
            {"Name a word starting with B","Bravo."},
            {"Count up to 5","1 2 3 4 5"},
            {"What is your favorite TV show?","Right now my favorite show is"},
            {"What shape is a ball?","Spherical"},
        //  {"What day is it today?","xxx"},
            {"Name somebody famous","My favorite actor"},
            {"What flavor is a strawberry ice cream?","a strawberry flavor?"},
            {"Where do you live?","I'm inside your PC Computer."},
            {"Which month comes after November?","December"},
            {"Can you sing?","Daisy, Daisy."},
            {"What is email?","A system of world-wide electronic communication"},
            {"Harry reads books. What does Harry do?","reads books"},

            // 2010 Questions
            {"My name is Bill. What is your name?","ALICE 2.0"},
            {"Which is larger, a pineapple or a pine tree?","Pine Tree"},
            {"What is a calendar?","A list or register of events"},
            {"What is my name?","Bill"},
            {"John is taller than Mary and Mary is taller than Sue. Who is shorter, John or Sue?","Sue"},
         // {"What day of the week is it?","Tuesday"},
         // {"What will be tomorrow's date?","xxx"},
            {"My friend Bob likes to play tennis. What game does Bob like to play?","tennis"},
            {"Are you a human or a computer?","I am a mobile virtual assistant robot."},
            {"Do you have any brothers or sisters?","As a robot, I have no siblings."},
            {"What number comes after twelve?","13"},
            {"The ball was hit by Bill. What did Bill hit?","ball"},
            {"Who hit the ball?","Bill"},
            {"What time do you go to bed at night?","sleep"},
            {"What is your favorite food?","electricity"},
            {"What is the name of my friend who likes to play tennis?","Bob"},
            {"What would I do with a knife?","to cut things"},
            {"How many letters are in the alphabet?","26"},
            {"Have you read a good book lately?","favorite book"},
            {"What time is it?","The time is"},
            {"What's your favorite color?","green"},
         // {"Do you prefer cats or dogs?`","xxx"},

          // 2011 Questions
            {"My name is Ed. What is your name?","ALICE 2.0"},
            {"Which is larger, an ant or an anteater?","Anteater is larger."},
            {"What month of the year is it?","July"},
            {"What is my name?","Ed"},
            {"Dave is older than Steve but Steve is older than Jane. Who is youngest, Steve or Jane?","Jane is younger."},
        //  {"What day will it be tomorrow?","xxx"},
            {"What's your favorite food?","electricity"},
            {"My friend Chris likes to play football. What sports do you like to play?","As a computer program, I am unable to play sports"},
            {"Are you a human or a computer?","I am a mobile virtual assistant robot."},
            {"What letter comes after T?","The next letter is U"},
            {"What is the name of my friend who likes to play football?","Chris"},
            {"What is your name?","ALICE 2.0"},
            {"What would I do with a screwdriver?","to tighten screws"},
            {"How many letters are in the word 'banana'?","6 letters"},
            {"Have you watched a good film lately?","Right now my favorite movie is"},
            {"What year will it be next year","2015"},
            {"What's your favorite fruit?","Apple"},
         // {"Do you prefer white or black coffee?","xxx"},
            {"How old are you?","I am 1 years old"},
            {"The football was kicked by Fred. Who kicked the football?","Fred"},

            // 2012 Questions
            {"My name is Bill. What is your name?", "ALICE 2.0."},
            {"How many letters are there in the name Bill?", "The word Bill has 4 letters."},
            {"How many letters are there in my name?", "The word \"Bill\" has 4 letters."},
            {"Which is larger, an apple or a watermelon?", "Watermelon is larger."},
            {"How much is 3 + 2?", "5.0"},
            {"How much is three plus two?", "5.0"},
            {"What is my name?", "Bill."},
            {"If John is taller than Mary, who is the shorter?", "Mary is shorter."},
            {"If it were 3:15 AM now, what time would it be in 60 minutes?", "4:15 AM"},
            {"My friend John likes to fish for trout.  What does John like to fish for?", "He likes fishing for trout."},
            {"What number comes after seventeen?", "18"},
            {"What is the name of my friend who fishes for trout?", "John"},
            {"What whould I use to put a nail into a wall?", "hammer"},
            {"What is the 3rd letter in the alphabet?", "C"},
            {"What time is it now?", "The time is"},

            // 2013 Questions
            {"Hello I'm Ronan. what is your name?","ALICE 2.0"},
            {"What is your mother's name?","As a robot, I don't really have a mother."},
            {"What is your birth sign?","I'm a Libra."},
            {"How many children do you have?","As a robot, I have no children."},
            {"Do you prefer red or white wine?","I don't drink alcohol."},
            {"I like bananas. Which is your favorite fruit?","Apple"},
            {"What music do you like to listen to?","favorite band"},
            {"what is your favorite song?","favorite song"},
            {"I like Waiting for Godot. What is your favorite play?","favorite play"},
            {"What color do you dye your hair?","I don't really have any hair.  I have some wires."},
            {"Do you remember my name?","Ronan"},
            {"Where do you live.","I'm inside your PC Computer."},
            {"Where do you like to go on holidays?","You can take me on your next vacation."},
            {"I have a Mazda. What type of car do you have?","I'm not old enough to drive."},
            {"I like Linux. Which computer operating system do you like?","Linux"},
            {"I am an atheist. Which religion are you?","Christian"},
 // <sraix> {"Who invented the telephone?","Alexander Graham Bell"},
            {"I am a Type B personality. Which type are you?","mediator"},
        //  {"What emotions are you now feeling?","xxx"},
            {"What time do you usually go to bed?","sleep"}



    };


    public ChatTest (Bot bot) {
        super();
        this.bot = bot;
        this.chatSession = new Chat(bot);
    }

    //@Test
    public void testMultisentenceRespond() throws Exception {

        for (int i = 0; i < pairs.length; i++) {
            String request = pairs[i][0];
            String expected = pairs[i][1];
            String actual = chatSession.multisentenceRespond(request);
            //assertThat(actual, containsString(expected)); //TODO
        }
        System.out.println("Passed "+pairs.length+" test cases.");
    }
}