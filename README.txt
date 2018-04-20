Steps for compilation:
Make sure java is in the Path environment 

Download the repository to whatever location you want
From that directory

if in a unix system
javac -classpath ".:./jsoup-1.11.3.jar" Main.java

replace : with ; n a Windows System


You can also set the classpath variable, but that depends on operating system and I'm not comfortable doing that.

Then, run java -classpath ".;./jsoup-1.11.3.jar" Main