Tools = {}

Tools.getFileAll = function(fileName)
  local f  = io.open(fileName, 'r')
  --luaTools:println("f")
 -- luaTools:println(f)
  if(f == nil) then
  	return nil
  end
  local content = f:read("*all")
  f:close()
  return content
end


--[[
	创建一个文件
	fileName 文件名字
	content  要写入的内容 
	isAdd 是否是追加内容
]]--
Tools.createFile = function(fileName , content , isAdd)
	local t = ""
	if(isAdd) then
		t = "a+"
	else
		t = "w"
	end
	local f = assert(io.open(fileName, t))
	f:write(content)
	f:close()
	Tools.println(fileName .. " file save suceess")
end

Tools.getIdsList = function(fileName)
	local content = Tools.getFileAll(fileName)
	if(content == nil ) then
		return nil
	end
	
	local strArr = Tools.split(content,"\n")
	local size = table.getn(strArr)
	--local idsMap = HashMap()
	local idsList = ArrayList()
	
	for i = 1  , size do
		local id = tonumber(string.sub(strArr[i],20))
		--local id = tonumber(strArr[i])
		if(idsList.contains(id) == false) then
			idsList.add(id)
		end
	end
	--size = table.getn(idsMap.toArray())
	--local size2 = idsMap.size()
	local size1 = idsList.size()
	--for i = 1 , size1 do
	--	print(idsList.get(i))
	--end
	
	return idsList
end

--[[
	字符串根据条件分数组
--]]
Tools.split = function (szFullString, szSeparator)
	local nFindStartIndex = 1
	local nSplitIndex = 1
	local nSplitArray = {}
	while true do
		local nFindLastIndex = string.find(szFullString, szSeparator, nFindStartIndex)
		if not nFindLastIndex then
            local val = string.sub(szFullString, nFindStartIndex, string.len(szFullString))
            if(val == "") then
                break
            end
			nSplitArray[nSplitIndex] = val
			break
		end
		
		nSplitArray[nSplitIndex] = string.sub(szFullString, nFindStartIndex, nFindLastIndex - 1)
		nFindStartIndex = nFindLastIndex + string.len(szSeparator)
		nSplitIndex = nSplitIndex + 1
	end
	return nSplitArray
end

Tools.getDateTimeStr = function()
	--local date=os.date("%Y-%m-%d %H:%M:%S");    --包含时分秒
	local date=os.date("%Y-%m-%d");				--只要年月日
	--Tools.println(date)
	return date
end


Tools.getDateTime = function()
	--local date=os.date("%Y-%m-%d %H:%M:%S");    --包含时分秒
	local date=os.date("%Y-%m-%d");				--只要年月日
	local arr = Tools.split(date,"-")
	local year , month , day 
	year = arr[1]
	month = arr[2]
	day = arr[3]
	--Tools.println(date)
	return year , month , day
end

Tools.println = function(msg)
	print(msg)
end
