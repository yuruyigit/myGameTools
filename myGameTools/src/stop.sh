#程序标识
programName=doraemon-gameLogic

programInfoString=`ps -Af | grep ${programName}| grep -v grep | grep -v SCREEN | grep -v bash`
processIdNameString=`ps -Af | grep ${programName}| grep -v grep | grep -v SCREEN | grep -v bash | awk '{print $2 ',' $NF}'`	 #获得程序pid 程序标识名，可能会是多个如：22152 game1 26136 game2

processIdNameArray=(${processIdNameString})		#id名称数组
processIdNameArrayLength=${#processIdNameArray[@]}		#id名称数组长度

echo -e "┏━━━━━━━━━━━━━━━\n${programInfoString}\n┗━━━━━━━━━━━━━━━"			#打印过滤出的进程信息
echo -e "processIdNameArrayLength-->"${processIdNameArrayLength}  " processIdNameString-->"${processIdNameString} "\n"

#执行结果
execResult="--> not found in the specified \"${programName}\" process !"

for((index=0; index < ${processIdNameArrayLength}; index++))
do
	pid=${processIdNameArray[${index}]}
	let index++
	name=${processIdNameArray[${index}]}

	echo "--> index ${index-1} at : ${pid}  ${name}" 

	if [ "${name}" = "${programName}" ]; then
	`kill -15 ${pid}`
		execResult="--> have this \"${programName}\"  at pid : ${pid} process, stop it !"
	fi
done

echo -e "\nexecResult at :" ${execResult} "\n"

