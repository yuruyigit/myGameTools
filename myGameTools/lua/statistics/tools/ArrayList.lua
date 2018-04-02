function ArrayList()
	local this = {}
	local array = {}
	local size = 1
	
	this.add = function(o)
		array[size] = o
		size = size + 1
		--return this.size()
	end
	
	this.addAll = function(list)
	   for i = size , list.size() do 
	       this.add(list.get(i))
	   end
	end

	this.insert = function (index , o )
		array[index] = o
        size = size + 1
        return this.size()
	end
	
	this.get = function (index)
		--local hero = array[index]
		--Tools.println(array[index].getName())
		return array[index]
	end
	
	this.removeByIndex = function(index)
		--[[
		array[index] = nil
		for i = index , size do 
			array[index] = array [index + 1]
		end
		local n = size - 1 
		if(n <= 0 ) then
		    size = 0 
		else
		    size = n
		end
		]]
        local obj = array [index]
		table.remove(array,index)
		size = size - 1
		
		return obj
	end
	
	this.removeByObject = function(o)
		local index = 0
		for i = 1 , size do
			if(array[i] == o) then
				index = i
			end
		end
		--Tools.println("index = "..index.." len = "..table.getn(array))
		if(index <= this.size() and index ~= 0) then
		      this.removeByIndex(index)
		end
	end
	
	--包含
	this.contains = function(o)
	   for i =1 , size do
	       if(array[i] == o) then
	           return true
	       end
	   end
	   
	   return false
	end

	this.clear = function ()
		for index = 0 , size do
			--array[i] = nil
            table.remove(array,1)
		end
--		size = 0
        size = 1
	end
	
    this.toArray = function()
        return array
    end
    
	this.size = function ()
--		return size - 1 
		return table.getn(array)
	end
	
	return this
end