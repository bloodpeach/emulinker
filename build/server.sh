LIB=`ls -1 lib/*.jar | xargs echo | sed "s/ /:/g"`
CLASSPATH="./components.xml:./conf:$LIB"
java -Xms64m -Xmx128m -cp $CLASSPATH org.emulinker.kaillera.pico.PicoStarter
