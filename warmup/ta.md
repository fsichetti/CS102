Go over basic info about yourself and what the recitations will look like.

Try an icebreaker, for example: tell the students why you went into CS and then ask them to talk in pairs as to why they did; after five minutes, ask some people to talk about why their partner went into CS (if the class is small enough, you can ask all the students to talk about their partners) 

Once the intro part is done, start with the Warmup Lab. The goals of this lab are:
- Refreshing basic Java syntax
- Familiarize the students with the recitation format
- Have students build up the habit of testing their programs
- Showing them how to retrieve recitation material and upload finished assignments to Gradescope
- Checking that everyone has their programming environment set up correctly
- Having students know each other

You should show them where to find the files and open them in your IDE/editor. Do challenge 1 live with them, asking for their input:
- start by reading the assignment and the scaffold;
- show them how to compile and run the incomplete program; explain why it compiles even though there is no solution;
- ask for ideas on how to solve it;
- code the solution live, compile+run;
- explain that it is important that they test their functions early and often, so have them suggest inputs for the program that they think are meaningful (the instructions already contain examples).

After testing Challenge 1 manually, explain how they can automate this by writing code that calls the function and checks the results. Challenge 1 contains a Test.java that does exactly that, with a set of cases already written in. Run it with `javac Test.java` and `java Test`, and show them the whole set checked in one command instead of one input at a time.

While the output is on screen, point at the three cases that expect 0. Those would have passed even against the empty placeholder, before any solution existed. Say out loud that this does not make them bad tests and does not mean the program is correct: a passing test only says it did not catch a bug this time, while a failing test proves something really is wrong.

At the start of Challenge 2, before anyone has written anything, open its Test.java and run it with them. It compiles and its one case fails, which is exactly what should happen. From there each folder has a Test.java with a single case written and a TODO; the students add the rest themselves.

Students should work on Challenge 2 and the remaining challenges in pairs (plus a group of 3 if odd). They should work on each problem together, NOT distribute work among themselves.

If students are reluctant to work in groups:
- if they ask you a question, ask them what their group members think
- walk around the room and ask people who their group members are, and if they discussed how to solve the problem
- explain to them that in real life, they will need to work with others, and this is a good way to practice those skills
- encourage them to talk but do not force them

In the final few minutes, show them how to upload the lab to Gradescope, which they will do once they finish the problems. This activity will not count toward the final grade, but they are encouraged to submit it anyway as if it were a graded lab. They will NOT be graded on their test quality; tests are a tool for them.