#!/usr/bash
javac -encoding utf-8 -cp $(echo ./libs/*.jar | tr ' ' ':') -d ./bin -sourcepath ./src src/com/vdbs/Base.java
# -Xlint:unchecked -Xlint:deprecation
cd bin

jar cvf0m test.jar manifest.txt com/vdbs/*.class

#java -classpath ../libs/*: com/vdbs/Base
#jar cvf0m test.jar manifest.txt 
java -jar test.jar

cd ..