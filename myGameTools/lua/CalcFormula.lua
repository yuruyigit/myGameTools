package.path = "./?.lua"
require "lua/Formula"
require "lua/statistics/RunStatistics"

--[[
	返回计算后的战力(战力计算统一处理）
--]]
function calcFighting(hp , def , atk , skillLevel)
	return hp / 5  + atk + def + skillLevel * 10;
end


--关于服务器的公式计算
--[[
useTime战斗使用的时间（单位：秒）
返回对应的战斗等级
--]]
function calcBattleLevel(useTime)
	if (useTime <= 15 ) then
		return 3
	elseif useTime <= 30 then
		return 2
	else
		return 1
	end
end

--[[
	随机数
		min 最小随机数
		max 最大随机数
--]]
function randomNo(min , max)
	math.randomseed(os.time())
	local n = 0
	for i=1,2 do
		n = math.random(max)%(max-min+1) + min;
	end
	return n
end
--[[

	计算敌军的掉落金钱的倍率
--]]
function calcEnemyDropCopperRandom()
	min=90;		--0.9
	max=110;	--1.1
	
	return randomNo(min,max)
end

--[[
	计算战斗奖励铜币随机
--]]
function calcBattleRandom()
	min=90;		--0.9
	max=110;	--1.1
	
	return randomNo(min,max)
end

--[[
	计算装备属性
	unit 传入的要添加属性的对象
	isAdd 是添加还是减少属性
--]]
function calcEquipProperty(unit , isAdd)
	
	local equipList = unit:getHeroControl():getEquipList() --装备列表
	local size =tonumber(equipList:size())-1
	
	for i = 0 , size do
		--luaTools:println(equipList:get(i))
		local equip = equipList:get(i)
		local item = equip:getItem()
		
		local proLen = item:getPropertySize() - 1;
		--luaTools:println("proLen = "..proLen)
		for j= 0 , proLen do
			local property = item:getPropertyVal(j)
			local propertyVal = item:getPropertyNumVal(j)
			--luaTools:println("j = "..j.." name = "..item:getName().." property = "..property.." propertyVal = "..propertyVal)
			--print("j = "..j.." name = "..item:getName().." property = "..property.." propertyVal = "..propertyVal)
			if (isAdd) then
				calcAddProperty(property,propertyVal,unit)
			else
				calcCostProperty(property,propertyVal,unit)
			end
		end
	end--]]
end

--[[
	计算添加装备属性
	property 添加属性的类型
	propertyVal 添加的属性值
	unit 要添加的属性的对象
--]]
function calcAddProperty(property , propertyVal , unit)
	
	--print("property = "..property.."  unit:getPhyAtk() = "..unit:getPhyAtk().. " propertyVal = "..propertyVal)
	if property == 1 then 					--生命
		local hp = unit:getHp() + propertyVal
		unit:setHp(hp)
	elseif (property == 2) then 			--物理攻击
		local phyAtk = unit:getPhyAtk() + propertyVal
		unit:setPhyAtk(phyAtk)
	elseif (property == 3) then				--法术攻击
		local magicAtk = unit:getMagicAtk() + propertyVal
		unit:setMagicAtk(magicAtk)
	elseif (property == 4) then				--物理防御
		local phyDef = unit:getPhyDef() + propertyVal
		unit:setPhyDef(phyDef)
	elseif (property == 5) then				--法术防御
		local magicDef = unit:getMagicDef() + propertyVal
		unit:setMagicDef(magicDef)
	elseif (property == 6) then 			--暴击
		local crit = unit:getCrit() + propertyVal
		unit:setCrit(crit)
	elseif (property == 7 ) then 			--抗暴击
		local resistCrit = unit:getResistCrit() + propertyVal
		unit:setResistCrit(resistCrit)
	elseif (property == 8 ) then			--暴击倍率
		local critMulriple = unit:getCritMulriple() + propertyVal
		unit:setCritMulriple(critMulriple)
	elseif (property == 9 ) then			--命中
		local hit = unit:getHit() + propertyVal
		unit:setHit(hit)
	elseif (property == 10) then			--闪避
		local dodge = unit:getDodge() + propertyVal
		unit:setDodge(dodge)
	elseif (property == 11) then			--攻速
		local atkSpeed = unit:getAtkSpeed() + propertyVal
		unit:setAtkSpeed(atkSpeed)
	elseif (property == 12) then			--真实伤害
		local realHurt = unit:getRealHurt() + propertyVal
		unit:setRealHurt(realHurt)
	elseif (property == 13) then			--最终免伤
		local finalAvoidHurt = unit:getFinalAvoidHurt() + propertyVal
		unit:setFinalAvoidHurt(finalAvoidHurt)
	elseif (property == 14) then			--生命回复
		local reverHp = unit:getRevertHp() + propertyVal
		unit:setRevertHp(reverHp)
	elseif (property == 15) then			--攻击距离
		local atkDist = unit:getAtkDist() + propertyVal
		unit:setAtkDist(atkDist)
	elseif (property == 16) then			--移动速度
		local moveSpeed = unit:getMoveSpeed() + propertyVal
		unit:setMoveSpeed(moveSpeed)
	elseif (property == 17) then			--回复比例
		local revertRate = unit:getRevertRate() + propertyVal
		unit:setRevertRate(revertRate)
	elseif (property == 18) then			--治疗比例
		local treatRate = unit:getTreatRate() + propertyVal
		unit:setTreatRate(treatRate)
	elseif (property == 19) then			--物理减伤率
		local phy_DRR = unit:getPhy_DRR() + propertyVal
		unit:setPhy_DRR(phy_DRR)
	elseif (property == 20) then			--法术减伤率
		local magic_DRR = unit:getMagic_DRR() + propertyVal
		unit:setMagic_DRR(magic_DRR)
	elseif (property == 106) then			--暴击率
		local critRate = unit:getCritRate() + propertyVal
		unit:setCritRate(critRate)
	elseif (property == 107) then			--抗爆率
		local resisCritRate = unit:getResistCritRate() + propertyVal
		unit:setResistCritRate(resisCritRate)
	elseif (property == 109) then			--命中率
		local hitRate = unit:getHitRate() + propertyVal
		unit:setHitRate(hitRate)
	elseif (property == 110) then			--闪避率
		local dodgeRate = unit:getDodgeRate() + propertyVal
		unit:setDodgeRate(dodgeRate)
	elseif (property == 210) then			--异常状态抵抗率
		local debuff_resist = unit:getDebuff_resist() + propertyVal
		unit:setDebuff_resist(debuff_resist)
	end
	--]]
end
--[[
	计算减少装备属性
	property 减少属性的类型
	propertyVal 减少的属性值
	unit 要减少的属性的对象
--]]
function calcCostProperty(property , propertyVal , unit)
	if property == 1 then 				--生命
		local hp = unit:getHp() - propertyVal
		unit:setHp(hp)
	elseif (property == 2) then 			--物理攻击
		local phyAtk = unit:getPhyAtk() - propertyVal
		unit:setPhyAtk(phyAtk)
	elseif (property == 3) then				--法术攻击
		local magicAtk = unit:getMagicAtk() - propertyVal
		unit:setMagicAtk(magicAtk)
	elseif (property == 4) then				--物理防御
		local phyDef = unit:getPhyDef() - propertyVal
		unit:setPhyDef(phyDef)
	elseif (property == 5) then				--法术防御
		local magicDef = unit:getMagicDef() - propertyVal
		unit:setMagicDef(magicDef)
	elseif (property == 6) then 				--暴击
		local crit = unit:getCrit() - propertyVal
		unit:setCrit(crit)
	elseif (property == 7 ) then 		--抗暴击
		local resistCrit = unit:getResistCrit() - propertyVal
		unit:setResistCrit(resistCrit)
	elseif (property == 8 ) then		--暴击倍率
		local critMulriple = unit:getCritMulriple() - propertyVal
		unit:setCritMulriple(critMulriple)
	elseif (property == 9 ) then		--命中
		local hit = unit:getHit() - propertyVal
		unit:setHit(hit)
	elseif (property == 10) then		--闪避
		local dodge = unit:getDodge() - propertyVal
		unit:setDodge(dodge)
	elseif (property == 11) then		--攻速
		local atkSpeed = unit:getAtkSpeed() - propertyVal
		unit:setAtkSpeed(atkSpeed)
	elseif (property == 12) then		--真实伤害
		local realHurt = unit:getRealHurt() - propertyVal
		unit:setRealHurt(realHurt)
	elseif (property == 13) then		--最终免伤
		local finalAvoidHurt = unit:getFinalAvoidHurt() - propertyVal
		unit:setFinalAvoidHurt(finalAvoidHurt)
	elseif (property == 14) then		--生命回复
		local reverHp = unit:getRevertHp() - propertyVal
		unit:setRevertHp(reverHp)
	elseif (property == 15) then		--攻击距离
		local atkDist = unit:getAtkDist() - propertyVal
		unit:setAtkDist(atkDist)
	elseif (property == 16) then		--移动速度
		local moveSpeed = unit:getMoveSpeed() - propertyVal
		unit:setMoveSpeed(moveSpeed)
	elseif (property == 17) then			--回复比例
		local revertRate = unit:getRevertRate() - propertyVal
		unit:setRevertRate(revertRate)
	elseif (property == 18) then			--治疗比例
		local treatRate = unit:getTreatRate() - propertyVal
		unit:setTreatRate(treatRate)
	elseif (property == 19) then			--物理减伤率
		local phy_DRR = unit:getPhy_DRR() - propertyVal
		unit:setPhy_DRR(phy_DRR)
	elseif (property == 20) then			--法术减伤率
		local magic_DRR = unit:getMagic_DRR() - propertyVal
		unit:setMagic_DRR(magic_DRR)
	elseif (property == 106) then			--暴击率
		local critRate = unit:getCritRate() - propertyVal
		unit:setCritRate(critRate)
	elseif (property == 107) then			--抗爆率
		local resisCritRate = unit:getResistCritRate() - propertyVal
		unit:setResistCritRate(resisCritRate)
	elseif (property == 109) then			--命中率
		local hitRate = unit:getHitRate() - propertyVal
		unit:setHitRate(hitRate)								
	elseif (property == 110) then			--闪避率
		local dodgeRate = unit:getDodgeRate() - propertyVal
		unit:setDodgeRate(dodgeRate)										
	elseif (property == 210) then			--异常状态抵抗率
		local debuff_resist = unit:getDebuff_resist() - propertyVal
		unit:setDebuff_resist(debuff_resist)											
	end
	--]]
end

string.split = function(s, p)
	local rt= {}
	string.gsub(s, '[^'..p..']+', function(w) table.insert(rt, w) end )
	return rt
end

function getSkillLevel(skillId , str)
	
	if(str == nil) then
		return 1
	end
	
	local str = string.split(str,"|")
	for k,v in pairs(str) do
		local e = string.split(v,",")
		--print(tonumber(e[2]))
		local id = tonumber(e[1])
		if(skillId == tonumber(e[1])) then
			return tonumber(e[2]);
		end
	end

end

--[[
	计算技能影响的属性值
		unit 影响的对象
--]]
function calcSkillProperty(unit)
	
	local skillList = unit:getSkillsList()
	local skillListSize = unit:getSkillsList():size() - 1 
	--luaTools:println("unit:getSkillStr() = "..unit:getSkillStr() .."  skillListSize = " ..skillListSize)
	for i = 0 , skillListSize do
		local skills = skillList:get(i):getSc()
		local effectSize = skills:getLevelUpEffectList():size() - 1
		--print("effectSize = "..effectSize)
		
		for j = 0 , effectSize do
			--print(j)
			
			local skillLevel = getSkillLevel(skills:getId(),unit:getSkillStr())
			--luaTools:println("skills:getId() = ")
			--luaTools:println(skills:getId())
			--luaTools:println("skillLevel = ")
			--luaTools:println(skillLevel)
			--luaTools:println(skills:getName())
			--luaTools:println(debug.traceback())
			
			if(skillLevel ~= 0 ) then			--LEVEL 等于0 说明这个这个技能还未开启
				--print("j = ")
				--print(j)
				local effect = skills:getLevelUpEffectList():get(j)
				local skill = skills:getSkill()
				
				local skillType = skill:getSkillType();			
				--luaTools:println("skillType = ".. skillType)
				if(skillType == 3) then 	--skillType = 3说明这个技能是影响人物战斗的属性技能 
					
					local property = effect:getPropertyTypeArr(j)
					local baseVal = effect:getNumericalArr(j)
					--基础值 + 等级 * 效果提升参数 (level_Up_Effect_Id,level_Up_Effect_Type ,level_Up_Effect_Numerical) 
					local paramUp = skill:getLevelUpEffectNumericalArr(j)
					
					--[[
					print("baseVal")
					print(baseVal)
					
					print("skillLevel")
					print(skillLevel)
					
					print("paramUp")
					print(paramUp)
					
					local propertyVal = baseVal + skillLevel * paramUp
					--print("paramUp = "..paramUp + " j = "..j)
					print("propertyVal = ")
					print(propertyVal)
					--]]
					
					--luaTools:println("skill.getName() = "..skill:getName().." skillLevel = ".. skillLevel.." baseVal = " ..baseVal .." paramUp = "..paramUp)
					local propertyVal = baseVal + (skillLevel - 1) * paramUp
					--luaTools:println(" property = "..property.." propertyVal = "..propertyVal.." name =" ..unit:getName().." hp = "..unit:getHp())
					--if(propertyVal == 100) then
					--	luaTools:println("skill.getName() = "..skill:getName().." skillLevel = ".. skillLevel.." baseVal = " ..baseVal .." paramUp = "..paramUp)
					--	luaTools:println("propertyVal = "..propertyVal)
					--	luaTools:println(debug.traceback())
					--end
					calcAddProperty(property , propertyVal , unit)
				end
			end
		end
	end
	
	
	local hp = unit:getHp();
	unit:setMaxHp(hp)
	
	--luaTools:println("unit:getMaxHp() = "..unit:getMaxHp())
end

--[[
	计算基础属性
		unit 传入的英雄战斗对象 (影响属性的对象)
--]]
function calcBaseProperty(unit)
	
	local hero = unit:getHero()	--hero配表对象
	local quality = unit:getQuality() --hero品质对象
		
	--比率
	local rate = 10000
	local level = unit:getLevel()
	
	
	local hp = calcBasePropertyFormula(hero:getHp(),hero:getHpGrowthBasic(),level,quality:getHpGrowth())
	local hpGrow = calcGrowVal(hero:getHpGrowthBasic(),quality:getHpGrowth())
	
	local phyDef = calcBasePropertyFormula(hero:getPhyDef(),hero:getPhyDefGrowthBasic(),level,quality:getPhyDefGrowth())
	local phyDefGrow = calcGrowVal(hero:getPhyDefGrowthBasic(),quality:getPhyDefGrowth())
	
	local phyAtk = calcBasePropertyFormula(hero:getPhyAtk(),hero:getPhyAtkGrowthBasic(),level,quality:getPhyAtkGrowth())
	local phyAtkGrow = calcGrowVal(hero:getPhyAtkGrowthBasic(),quality:getPhyAtkGrowth())
	
	local magicAtk = calcBasePropertyFormula(hero:getMagicAtk() , hero:getMagicAtkGrowthBasic(),level,quality:getMagicAtkGrowth())
	local magicAtkGrow = calcGrowVal(hero:getMagicAtkGrowthBasic(),quality:getMagicAtkGrowth())
	
	local magicDef = calcBasePropertyFormula(hero:getMagicDef(),hero:getMagicDefGrowthBasic(),level,quality:getMagicDefGrowth())
	local magicDefGrow = calcGrowVal(hero:getMagicDefGrowthBasic(),quality:getMagicDefGrowth())
	
	--hp = 基础血量 + 基础血量成长/10000 * 等级 *　血量成长系数
	--local hp  = hero:getHp() + hero:getHpGrowthBasic() / rate * unit:getLevel() * quality:getHpGrowth()
	--phyDef = 基础防御 + 基础防御成长 /10000 * 等级 * 防御成长系数
	--local phyDef  = hero:getPhyDef() + hero:getPhyDefGrowthBasic() / rate * unit:getLevel() * quality:getPhyDefGrowth()
	--phyAtk = 基础攻击 + 基础攻击成长 /10000 * 等级 * 攻击成长系数
	--local phyAtk  = hero:getPhyAtk() + hero:getPhyAtkGrowthBasic() / rate * unit:getLevel() * quality:getPhyAtkGrowth()
	--magicAtk = 基础法攻 + 基础法攻成长 /10000 * 等级 * 法攻成长系数
	--local magicAtk = hero:getMagicAtk() + hero:getMagicAtkGrowthBasic() / rate * unit:getLevel() * quality:getMagicAtkGrowth()
	--magicDef = 基础法防 + 基础法防成长 /10000 * 等级 * 法防成长系数
	--local magicDef = hero:getMagicDef() + hero:getMagicDefGrowthBasic() / rate * unit:getLevel() * quality:getMagicDefGrowth()


	--luaTools:println("hero:getMoveSpeed() = "..hero:getMoveSpeed())
	unit:setHp(hp)
	unit:setPhyDef(phyDef)
	unit:setPhyAtk(phyAtk)
	unit:setMagicAtk(magicAtk)
	unit:setMagicDef(magicDef)
	unit:setAtkDist(hero:getAtkDistance())
	unit:setMoveSpeed(hero:getMoveSpeed())
	unit:setHpGrow(hpGrow)
	unit:setPhyDefGrow(phyDefGrow)
	unit:setPhyAtkGrow(phyAtkGrow)
	unit:setMagicAtkGrow(magicAtkGrow)
	unit:setMagicDefGrow(magicDefGrow)
	unit:setCrit(0)
	unit:setResistCrit(0)
	unit:setCritMulriple(0)
	unit:setHit(0)
	unit:setDodge(0)
	unit:setAtkSpeed(0)
	unit:setRealHurt(0)
	unit:setFinalAvoidHurt(0)
	unit:setRevertHp(0)
	unit:setHitRate(0)
	unit:setDodgeRate(0)
	unit:setCritRate(0)
	unit:setResistCritRate(0)
	unit:setPhy_DRR(0)
	unit:setMagic_DRR(0)
	unit:setDebuff_resist(0)
	unit:setRevertRate(10000)
	unit:setTreatRate(10000)
	unit:setAnger(0)
	
end

--[[ 
初始化英雄的属性值
	unit 传入的英雄战斗对象
--]] 
function calcInitHeroProperty(unit)
	
	--luaTools:println("calcBaseProperty")
	calcBaseProperty(unit)
	--luaTools:println("1unit:getRealHurt() = "..unit:getRealHurt())
	
	calcEquipProperty(unit , true)
	--luaTools:println("2unit:getRealHurt() = "..unit:getRealHurt())
	
	calcSkillProperty(unit)
	--luaTools:println("3unit:getRealHurt() = "..unit:getRealHurt())
	--luaTools:println("calcSkillProperty")
	
	
end

function test()
	
	--[[
	coroutine.create(function ()
		while
	end)
	--]]
end



function LUA_TABLE_PRINT( t, indent )
    local pre = string.rep("\t", indent)
    for k,v in pairs(t) do
        if type(v) == "table" then
            if type(k) == "number" then
                luaTools:println(pre .. "[" .. k .. "]" .. " = {")
                LUA_TABLE_PRINT(v, indent + 1)
                luaTools:println(pre .. "},")
            elseif type(k) == "string" then
                if tonumber(k) then
                    luaTools:println(pre .. "[\"" .. k .. "\"] = {")
                elseif (tonumber(string.sub(k, 1, 1))) then
                    luaTools:println(pre .. "[\"" .. k .. "\"] = {")
                else
                    luaTools:println(pre .. k .. " = {")
                end
                LUA_TABLE_PRINT(v, indent + 1)
                luaTools:println(pre .. "},")
            end
        elseif type(v) == "number" then
            if type(k) == "number" then
                luaTools:println(pre .. "[" .. k .. "]" .. " = " .. v .. ",")
            elseif type(k) == "string" then
                if tonumber(k) then
                    luaTools:println(pre .. "[\"" .. k .. "\"] = " .. v .. ",")
                elseif (tonumber(string.sub(k, 1, 1))) then
                    luaTools:println(pre .. "[\"" .. k .. "\"] = " .. v .. ",")
                else
                    luaTools:println(pre .. k .. " = " .. v .. ",")
                end
            end
        elseif type(v) == "string" then
            local text = string.gsub(v, "[\n]", "")
            text = string.gsub(text, "\"", "\\\"")
            if type(k) == "number" then
                luaTools:println(pre .. "[" .. k .. "]" .. " = \"" .. text .. "\",")
            elseif type(k) == "string" then
                if tonumber(k) then
                    luaTools:println(pre .. "[\"" .. k .. "\"] = \"" .. text .. "\",")
                elseif (tonumber(string.sub(k, 1, 1))) then
                    luaTools:println(pre .. "[\"" .. k .. "\"] = \"" .. text .. "\",")
                else
                    luaTools:println(pre .. k .. " = \"" .. text .. "\",")
                end
            end
        end
    end
end

function cclogTable(tables)
	-- body
	-- if type(tables) ~= "table" then 
	-- 	 print("cclogTable参数为 nil")
	-- 	return 
	-- end 
	-- for key,value in pairs(tables) do
	-- 	if type(value) == "table" then
	-- 		print("------------"..key.."-----------------")
	-- 		cclogTable(value)
	-- 	else
	-- 		print(key..":"..value)
	-- 	end
	-- end
    if tables then 
        luaTools:println("{")
        LUA_TABLE_PRINT(tables,1)
        luaTools:println("}")
    else 
        cclog("Error:","打印table为nil")
    end 
end


--[[
	计算单位的战斗力
		unit 传入要计算谁的战斗力对象
--]]
function calcFighting(unit)

	local fighting = 0
	--比率数组
	local ratioArr = {}
	ratioArr[1]= 0.1		--生命值
	ratioArr[2]= 1			--物理攻击
	ratioArr[3]= 1			--法术攻击
	ratioArr[4]= 1			--物理防御
	ratioArr[5]= 1			--法术防御
	ratioArr[6]= 1			--暴击
	ratioArr[7]= 1			--抗暴击
	ratioArr[8]= 0.15		--暴击倍率
	ratioArr[9]= 1			--命中
	ratioArr[10]= 1			--闪避
	ratioArr[11]= 0.1		--攻速
	ratioArr[12]= 2			--真实伤害
	ratioArr[13]= 2			--最终免伤
	ratioArr[14]= 1			--生命回复 
	
	local propertyArr = {}
	propertyArr[1] = unit:getHp()
	propertyArr[2] = unit:getPhyAtk()
	propertyArr[3] = unit:getMagicAtk()
	propertyArr[4] = unit:getPhyDef()
	propertyArr[5] = unit:getMagicDef()
	propertyArr[6] = unit:getCrit()
	propertyArr[7] = unit:getResistCrit()
	propertyArr[8] = unit:getCritMulriple()
	propertyArr[9] = unit:getHit()
	propertyArr[10] = unit:getDodge()
	propertyArr[11] = unit:getAtkSpeed()
	propertyArr[12] = unit:getRealHurt()
	propertyArr[13] = unit:getFinalAvoidHurt()
	propertyArr[14] = unit:getRevertHp()
	
	--cclogTable(propertyArr)
	for i = 1 , 14 do
		local f = calcFightingFormula(propertyArr[i],ratioArr[i])
		
		fighting = fighting+ f
	end
	
	--下面是关于技能的战力计算
	local skillList = unit:getSkillsList()
	local skillListSize = unit:getSkillsList():size() - 1 
	for i = 0 , skillListSize do
		local skills = skillList:get(i):getSc()
		local skillLevel = getSkillLevel(skills:getId(),unit:getSkillStr())
		if(skillLevel ~= nil) then
			fighting = fighting + skillLevel *  skills:getSkill():getPowerValue()
		end
	end
	
	--luaTools:println("name = "..unit:getName().." fighting = "..fighting)
	return fighting
	
end

----yb 
--函数命名规范 calc打头

--[[
	随机数
		min 最小随机数
		max 最大随机数
--]]
function randomNo(min , max)

	math.randomseed(os.time())
	local n = 0
	for i=1,2 do
		n = math.random(max)%(max-min+1) + min;
	end
	return n
	
	--[[
	math.randomseed(os.time())
	local n = math.random(min,max)
	return math.ceil(n)
	]]
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

--[[
	计算商品要显示的哪些物品的随机数
--]]
function calcShopRandomFormula()
	return 1;
end

--[[
	计算战斗所扣除的体力
	needPhysical 该场战斗所需要的体力
]]
function calcBattleCostPhysical(needPhysical)
	return (needPhysical - 1) * 1 / 6 + 1 ;
end

--[[
	pvp和排位赛的经验奖励
--]]
function calcRankPvpRewardExpFormula()
	return 1
end


