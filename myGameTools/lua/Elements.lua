--module("Elements",package.seeall);

Elements={}
-- 计算技能的基础伤害值
function Elements:getBasicDamage( elements , property_Type , attUnit , reUnit, numerical , parameters1 )
	elements = tonumber(elements)
	property_Type = tonumber(property_Type)
	numerical = tonumber(numerical)
	parameters1 = tonumber(parameters1)
	
	local basicDamage = 0 	
	if elements == 100 then
		basicDamage = numerical --直接造成numerical的伤害
	elseif elements == 101 then
		if property_Type == 1 then
			basicDamage = attUnit:getPhyAtk() * numerical / 10000	--造成比例物理伤害
		elseif property_Type == 2 then
			basicDamage = attUnit:getMagicAtk() * numerical / 10000	--造成比例法术伤害
		end
	elseif	elements == 102 then
		if property_Type == 1 then
			basicDamage = attUnit:getPhyAtk() * parameters1 / 10000 + numerical		--造成比例和附加物理伤害
		elseif property_Type == 2 then
			basicDamage = attUnit:getMagicAtk() * parameters1 / 10000 + numerical	--造成比例和附加法术伤害
		end
	elseif elements == 103 then
		basicDamage = attUnit:getMaxHp() * numerical / 10000	--造成攻击者血量上限比例的伤害	
	elseif elements == 104 then
		if property_Type == 1 then
			basicDamage = attUnit:getPhyDef() * numerical / 10000	--造成攻击者物理防御力比例的伤害
		elseif property_Type == 2 then
			basicDamage = attUnit:getMagicDef() * numerical / 10000	--造成攻击者法术防御力比例的伤害
		end
	elseif elements == 105 then
		basicDamage = reUnit:getMaxHp() * numerical / 10000		--造成防守方血量上限比例的伤害	
	elseif elements == 106 then
		basicDamage = reUnit:getHp() * numerical / 10000		--造成防守方残余血量比例的伤害	
	elseif elements == 107 then
		if property_Type == 1 then
			basicDamage = attUnit:getPhyAtk() * ( 1 + ( reUnit:getMaxHp() - reUnit:getHp() ) / reUnit:getMaxHp() ) --对方剩余血量越少，攻击力越高
		elseif property_Type == 2 then
			basicDamage = attUnit:getMagicAtk() * ( 1 + ( reUnit:getMaxHp() - reUnit:getHp() ) / reUnit:getMaxHp() ) --对方剩余血量越少，攻击力越高
		end
	elseif elements == 108 then
		if property_Type == 1 then
			if	reUnit:getHp() / reUnit:getMaxHp() < parameters1 then
				basicDamage = attUnit:getPhyAtk() * numerical / 10000
			else
				basicDamage = attUnit:getPhyAtk() * numerical / 30000		--目标血量低于一定比例，造成比例物理伤害，否则造成1/3物理伤害
			end
		elseif property_Type == 2 then
			if reUnit:getHp() / reUnit:getMaxHp() < parameters1 then
				basicDamage = attUnit:getMagicAtk() * numerical / 10000
			else
				basicDamage = attUnit:getMagicAtk() * numerical / 30000		--目标血量低于一定比例，造成比例法术伤害，否则造成1/3法术伤害
			end
		end
	end
	
	return basicDamage
	
end

-- 计算技能的基础回复值
function Elements:getBasicHeal( attUnit , numerical , property_Type , parameters1 )
	local basicHeal = 0
	if elements == 200 then
		basicHeal = numerical		--回复参数的生命
	end
	if elements == 201 then
		basicHeal = attUnit.magic_att * numerical / 10000		--回复法术攻击比例的生命
	end
	if elements == 202 then
		basicHeal = attUnit.magic_att * parameters1 /10000 + numerical		--回复法术比例附加参数的生命
	end
	if elements == 203 then
		basicHeal = attUnit.originalHp * numerical / 10000		--回复发动者生命上限比例的生命
	end
	if elements == 204 then
		if property_Type == 1 then
			basicHeal = attUnit.phy_def * numerical / 10000
		elseif property_Type == 2 then
			basicHeal = attUnit.magic_def * numerical / 10000	--回复对应类型防御力比例的生命
		end
	end
	return basicHeal
end

-- 返回是否命中
function Elements:getHitRate( attUnit , reUnit,add_Property , add_Numerical , parameters2 , attSkill )
	local finalHit = 0 -- 最终命中率
	local isHit = false --是否命中
	--特殊命中算法
	if parameters2 == 1 then
		finalHit = attSkill.skillLv / reUnit.level * 10000 - reUnit.debuff_resist
		if math.random(1,10000) <= finalHit then
			isHit = true
		end
	else
		local add_Numerical1 = add_Numerical -- 技能命中值
		local add_Numerical2 = add_Numerical -- 技能命中率
		local level = 0 -- 采用谁的等级
		if add_Property == 9 then
			add_Numerical2 = 0
		elseif add_Property == 109 then
			add_Numerical1 = 0
		else
			add_Numerical1 = 0
			add_Numerical2 = 0
		end
		
		if attUnit.hitRate + add_Numerical1 - reUnit.dodgeRate < 0 then
			level = attUnit.level
		else
			level = reUnit.level
		end
	
		finalHit = 9000 + attUnit.hitRate - reUnit.dodgeRate + add_Numerical2 + (attUnit.hit + add_Numerical1 -reUnit.miss) / math.pow(1.04,level) * 100 
		if math.random(1,10000) <= finalHit then
			isHit = true
		end
	end
	return isHit
end

-- 返回暴击倍率
function Elements:getCritPower( attUnit , reUnit , add_Property , add_Numerical )
	add_Property = tonumber(add_Property)
	local add_Numerical1 = add_Numerical --技能暴击值
	local add_Numerical2 = add_Numerical --技能暴击率
	local add_Numerical3 = add_Numerical --技能暴击倍率值
	local level = 0 -- 采用谁的等级
	local finalCrit = 0 --最终暴击率
	local crit_power = 10000 --最终暴击倍率
	if add_Property == 6 then
		add_Numerical2 = 0
	elseif add_Property == 106 then
		add_Numerical1 = 0
	else
		add_Numerical1 = 0
		add_Numerical2 = 0
	end
	if add_Property ~= 8 then
		add_Numerical3 = 0
	end
	if attUnit:getCrit() + add_Numerical1 - reUnit:getResistCrit() < 0 then
		level = attUnit:getLevel()
	else
		level = reUnit:getLevel()
	end
	
	finalCrit = 500 + attUnit:getCritRate() - reUnit:getResistCritRate() + add_Numerical2 + (attUnit:getCrit() + add_Numerical1 - reUnit:getResistCrit()) / math.pow(1.04,level) * 100 
	
	if math.random(1,10000) <= finalCrit then
		crit_power = attUnit:getCritMulriple() + 15000 + add_Numerical3
	end
	return crit_power
end

-- 防守方免伤率
function Elements:getDamageReduceRate( property_Type , attUnit , reUnit )
	local DamageReduceRate = 0
	property_Type = tonumber(property_Type)
	if property_Type == 1 then
		DamageReduceRate = reUnit:getPhyDef() / (reUnit:getPhyDef() + attUnit:getLevel() * 25)
	elseif property_Type == 2 then
		DamageReduceRate = reUnit:getMagicDef() / (reUnit:getMagicDef() + attUnit:getLevel() * 25)
	end
	return DamageReduceRate
end

-- 最终造成的伤害
function Elements:getFinalDamage( attUnit , reUnit, basicDamage , property_Type , add_Numerical )
	property_Type = tonumber(property_Type)
	local FinalDamage = 0
	local DamageReduceRate = 0 
	local getDamageReduceRate = self:getDamageReduceRate(property_Type,attUnit,reUnit)
	local critPower = self:getCritPower(attUnit,reUnit,add_Property,add_Numerical)
	
	if property_Type == 1 then
		DamageReduceRate = reUnit:getPhy_DRR()
	else
		DamageReduceRate = reUnit:getMagicDef()
	end
	FinalDamage = (basicDamage * (1 - getDamageReduceRate - DamageReduceRate) + attUnit:getRealHurt() - reUnit:getFinalAvoidHurt()) * critPower / 10000
	--print("basicDamage = "..basicDamage.." getDamageReduceRate = "..getDamageReduceRate.." DamageReduceRate = "..DamageReduceRate)
	--print("attUnit:getRealHurt() = "..attUnit:getRealHurt().." reUnit:getFinalAvoidHurt() = "..reUnit:getFinalAvoidHurt().." critPower = "..critPower)
	if FinalDamage <= 0 then
		FinalDamage = 1
	end
	
	local res = {}
	res["FinalDamage"] = FinalDamage
	if critPower > 10000 then
		res["isCrit"] = true
	else
		res["isCrit"] = false
	end
	return res
end

--最终回血
function Elements:getFinalHeal( attUnit , reUnit , basicHeal , property_Type , add_Numerical )
	local FinalHeal = 0
	local getDamageReduceRate = self:getDamageReduceRate(property_Type,attUnit,reUnit)
	local critPower = self:getCritPower(attUnit,reUnit,add_Property,add_Numerical) 
	FinalHeal = basicDamage * (critPower / 10000) * (1 + attUnit.treatRate/10000 + reUnit.revertRate / 10000)
	
	local res = {}
	res["FinalHeal"] = FinalHeal
	if critPower > 10000 then
		res["isCrit"] = true
	else
		res["isCrit"] = false
	end
	return res
end

-- 加速速率
function Elements:getSpeedRate( attUnit )
	local speedRate
	speedRate = attUnit.att_speed / 10000
	return speedRate
end

function getBasicDamage( elements , property_Type , attUnit , reUnit , numerical , parameters1 )
	return Elements:getBasicDamage( elements , property_Type , attUnit , reUnit , numerical , parameters1 )
end

function getFinalDamage( attUnit , reUnit, basicDamage , property_Type , add_Numerical )
	return Elements:getFinalDamage( attUnit , reUnit, basicDamage , property_Type , add_Numerical )
end
