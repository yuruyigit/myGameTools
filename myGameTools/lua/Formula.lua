
--函数命名规范 calc打头
luaTools = luajava.bindClass("hssg.tools.lua.LuaTools")

function errFunc(msg)
	local msgInfo = "----------------------------------------\nLUA ERROR : " .. tostring(msg) .. "\n"..debug.traceback()
	luaTools:luaError(msgInfo)
    --luaTools:println("----------------------------------------")
	--luaTools:println("LUA ERROR: " .. tostring(msg) .. "\n")
    --luaTools:println(debug.traceback())
    return msg
end

function init(v)
	
	--print("v = "..v)
	--luaTools:println("v = "..v:getName())
	--v:setName("李四")
	--luaTools:println("init")
	
	return 323, 1234
end

--[[
	随机数
		min 最小随机数
		max 最大随机数
--]]
function randomNo(min , max)
	--[[
	math.randomseed(os.time())
	local n = 0
	for i=1,2 do
		n = math.random(max)%(max-min+1) + min;
	end
	return n
	]]
	return luaTools:randomNo(min,max)
end

function randomNo1(min , max)
	
	local n = 0
	for i=1,2 do
		n = math.random(max)%(max-min+1) + min;
	end
	return n
	
end

--[[
	计算基础属性公式
		base 基础值
		growBase 基础成长值
		level 等级
		growth 成长系数
--]]
local rate = 10000;
function calcBasePropertyFormula(base , growBase , level, growth)
	return base + growBase / rate * level * growth; 
end 

--[[
	计算成长值
	growBase 基础成长值
	growth 成长系数
--]]
function calcGrowVal(growBase ,growth )
	return growBase / rate * growth
end

--[[	
	计算战斗力公式
		propertyVal对应属性的值
		ratio 对应属性的比率
-]]
function calcFightingFormula(propertyVal, ratio)
	return propertyVal * ratio
end

--[[
	计算强化消耗的铜币
		refVal 对应物品强化的精炼值
		copperBase 这件装备强化表中的基础铜币花费
--]]
function calcIntensifyCostCopperFormula(refVal , copperBase)
	return  refVal * copperBase
end

--随机的属性类型的数组
RANDOM_PROPERTY_ARR = {1,2,3,4,5,6,7,8,9,10,11,12,13,14}
PROPERTY_SIZE = table.getn(RANDOM_PROPERTY_ARR)
--[[
	随机装备的强化与洗练属性类型
--]]
function calcPropertyTypeFormula()
	-- 1-16 对应item表中的property里面的类型
	math.randomseed(os.time())
	local post = randomNo(1,PROPERTY_SIZE)
	--local post1 = randomNo1(1,PROPERTY_SIZE)
	--luaTools:println("1============>post = "..post.."  post1 = "..post1)
	return RANDOM_PROPERTY_ARR[post]
end

--[[
	强化装备和洗练计算的属性值公式
		maxVal 最大随机取值范围
		return 属性值
--]]
function calcPropertyValFormula(maxVal)
	--luaTools:println("1============>maxVal = "..maxVal)
	--print("2============>maxVal = "..maxVal)
	
	--return randomNo(1 , maxVal)
end


--[[
	计算商品要显示的哪些物品的随机数
--]]
function calcShopRandomFormula()
	return 1;
end

--[[
	pvp和排位赛的经验奖励
--]]
function calcRankPvpRewardExpFormula()
	return 1
end

--[[
	计算元宝兑换的铜币数量
	type 兑换的类型，1 一次，2多次 
	level 君主等级
	costCount 可以兑换次数
	curCount 当前的次数
]]
--铜币基数
local baseCopper = 20000
function calcIngotExchangeCopper(types , level , curCount,costCount)
	local total = 0 
	--result[1].crit是否暴击，0未暴击 ， 1 低暴击 ， 2 高暴击
	local result = {total=0,arrVal={}}
	
	if ( types == 1) then				-- 1次
		local rate = randomNo(1,10000)
		local  p = "1"
		result.arrVal[p] = {}
		if(rate <= 6000) then 			--不暴击
			total = baseCopper + level * 100
			result.arrVal[p].crit=0
			result.arrVal[p].val=total
			result.arrVal[p].count=curCount + 1 
		else							--暴击
			if(rate > 6000 and rate <= 9000) then
				total = (baseCopper + level * 100) * 2
				result.arrVal[p].crit=1
				result.arrVal[p].val=total
				result.arrVal[p].count=curCount + 1 
			else
				total = (baseCopper + level * 100) * 3
				result.arrVal[p].crit=2
				result.arrVal[p].val=total
				result.arrVal[p].count=curCount + 1 
			end
		end
	elseif (types == 2) then				--多次
		--luaTools:println("len = "..costCount)
		math.randomseed(os.time())
		for  i = 1 , costCount do
			local rate = randomNo1(1,10000)
			local value = 0 
			local p = i..""
			result.arrVal[p] = {}
			--luaTools:println(" p = "..p.." rate = "..rate)
			if(rate <= 6000) then			--不暴击
				value = baseCopper + level * 100
				result.arrVal[p].crit=0
				result.arrVal[p].val=value
				result.arrVal[p].count=curCount + i 
			else
				if(rate > 6000 and rate <= 9000) then
					value = (baseCopper + level * 100) * 2
					result.arrVal[p].crit=1
					result.arrVal[p].val=value
					result.arrVal[p].count=curCount + i 
				else
					value = (baseCopper + level * 100) * 3
					result.arrVal[p].crit=2
					result.arrVal[p].val=value
					result.arrVal[p].count=curCount + i 
				end
			end
			total = total + value
		end
	end
	result.total = total
	return result
end