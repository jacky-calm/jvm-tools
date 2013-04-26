cd ~/scripts/java
sudo cp -R /media/sf_share/src/com ./
sudo chmod 777 -R com
javac -cp ./lib/tools.jar com/sun/tools/example/debug/tty/ThreadDumper.java
java -cp .:./lib/tools.jar com.sun.tools.example.debug.tty.ThreadDumper  -attach 8453
