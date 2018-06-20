#程序标识
programName=qyc_usercenter_gc

#执行结果
execResult="--> not found in the specified \"${programName}\" process !"

pId=`ps -ef | grep ${programName} | grep -v grep | awk '{print $2}'`

echo -e "pId at :" ${pId} "\n"

if [ ! -z ${pId} ]; then
	kill -15 ${pId}
	execResult="--> have this \"${programName}\"  at pId : ${pId} process, stop it !"
fi

echo -e "\nexecResult at :" ${execResult} "\n"

