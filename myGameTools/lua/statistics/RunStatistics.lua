require "lua/statistics/tools/Libs"
--函数命名规范 calc打头
--luaTools = luajava.bindClass("game.tools.lua.LuaTools")
--[[
	计算登率
	login_today 今天天登录日期文件
	login_before	昨天登录日期文件
	dateStr 要计算的当前日期
	i 传入的几登
--]]
function loginRate(login_today , login_before , dateStr , i)						--登录率
	--默认初始化登率
	local default_init_rate = i.."登率为:".."0 \t"
	--luaTools:println("login_today = " ..login_today.. " login_before = "..login_before)
	--获得：1天前注册用户数组&今天登录用户数组
	local loginIdArr_today = Tools.getIdsList(login_today)
	if(loginIdArr_today == nil ) then
		return default_init_rate
	end
	
	local loginIdArr_before = Tools.getIdsList(login_before)
	if(loginIdArr_before == nil ) then
		return default_init_rate
	end
	
	local size1 = loginIdArr_before.size()					
	local size2 = loginIdArr_today.size()
	
	--比较：重合的部分就是登录的人数
	local count = 0
	for i = 1 , size1 do
		local id = loginIdArr_before.get(i)
		if(loginIdArr_today.contains(id) == true) then
			count = count + 1 
		end
	end

	--计算：登率
	local result = count / size1
	--luaTools:println("count = "..count .." result = " ..result)
	if(count ~= 0 )then
		return i.."登率为: "..(result*100).."%\t"
	else
		return default_init_rate
	end
	--luaTools:println("count = " .. count);
end

--[[
	计算登率
	loginCount 要计算的当前是几登率
--]]
function loginRate(loginCount)						--登录率
	
	local loginFileStr = "logs/login_"..Tools.getDateTimeStr()..".log";
	loginCount = loginCount - 1;
	
	--获得当天的登录列表(已经去重复)
	local todayLoginList = Tools.getIdsList(loginFileStr)
	if(todayLoginList == nil ) then
		luaTools:println( loginFileStr .." is empty !");
		return
	end
	
	luaTools:println("todayLoginList.size() ".. todayLoginList.size())
	
	--当天的登录数
	local size = todayLoginList.size()
	
	for i = 1 , loginCount do
		local dateStr = luaTools:getCostDate(Tools.getDateTimeStr() , -i)
		luaTools:println("logs/login_"..dateStr..".log");
		local oldLoginList = Tools.getIdsList("logs/login_"..dateStr..".log")
		luaTools:println("oldLoginList.size() = "..oldLoginList.size());
		
		--[[
		if(oldLoginList ~= nil) then
		
		else
			 
		end
		--]]
	end
end

--[[
	执行数据统计
		appointDate 指定的时间日期 格式:	年-月-日
--]]
function runStatistics(appointDate)
	
	--luaTools:println("appointDate = "..appointDate)
	--记录日期
	--local content = "日期:"..Tools.getDateTimeStr().."\n"
	local content = appointDate.."\t"
	
	--luaTools:println(content);
	local today = luaTools:getCostDate(appointDate , -1)
	--计算登率
	for i = 1 , 6 do
		local dateStr = luaTools:getCostDate(today , -i)
		--luaTools:println(i.."_dateStr = "..dateStr)
		content = content .. loginRate("logs/login_"..today..".log","logs/login_"..dateStr..".log", dateStr , i + 1 )
	end
	

	--命名记录文件
	--local fileName = "logs/statistics_"..Tools.getDateTimeStr()..".log"
	local fileName = "logs/statistics_"..today..".log"
	
	Tools.createFile(fileName,content, false)
	
	--luaTools:println("\n\n\n" .. content)
end



function runStatistics()
	
	--记录日期
	local content = "日期:"..Tools.getDateTimeStr().."\n"
	--[[计算登率
	for i = 1 , 6 do
		local dateStr = luaTools:getCostDate(Tools.getDateTimeStr() , -i)
		luaTools:println(i.."_dateStr = "..dateStr)
		content = content .. loginRate("logs/login_"..today..".log","logs/login_"..dateStr..".log", dateStr , i +1 )
	end
	--]]
	
	loginRate(2)
	--loginRate("logs/login_"..today..".log",3)
	--loginRate("logs/login_"..today..".log",7)
	
	--命名记录文件
	--local fileName = "logs/statistics_"..Tools.getDateTimeStr()..".log"
	--Tools.createFile(fileName,content, false)
	
end

runStatistics()