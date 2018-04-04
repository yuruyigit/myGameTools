#/bin/sh

java -Xdebug -Xrunjdwp:transport=dt_socket,address=8001,server=y -jar -Xmx2000m -Xms2000m -Xmn1g gameLogic.jar doraemon-gameLogic
