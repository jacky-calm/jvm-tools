javac -cp .././lib/sa-jdi.jar com/jvm/dump/tools/ClassDumper.java
java -cp .:.././lib/sa-jdi.jar -Dpattern=HelloEJB3 -Dout=. com.jvm.dump.tools.ClassDumper 4567

