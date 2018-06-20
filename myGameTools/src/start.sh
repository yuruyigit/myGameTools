source /root/.bash_profile
#-Xloggc:qyc-usercenter_gc.log 这里使用qyc-usercenter_gc.log做为，程序唯一的标识,stop.sh，会根所唯一标识来进行关闭进程

java -jar -Xmx2000m -Xms2000m -Xmn1g -Xloggc:myGameTools_gc.log gameLogic.jar