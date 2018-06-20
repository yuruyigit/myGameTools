#/bin/sh

java -Xdebug -Xrunjdwp:transport=dt_socket,address=8001,server=y -Xloggc:myGameTools_gc.log -jar -Xmx2000m -Xms2000m -Xmn1g gameLogic.jar
