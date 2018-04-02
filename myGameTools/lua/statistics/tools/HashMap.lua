function HashMap()
	local this = {}
	local map = {}
	this.put = function (k , v)
		map[k] = v
	end

	this.get = function(k)
		return map[k]
	end

	this.values = function()
		return map
	end

	this.size = function ()
		return table.getn(map)
	end
	
	this.toArray = function()
		return map
	end
	
    this.clear = function ()
        local size = this.size()
        for index = 0 , size do
            table.remove(map,1)
        end
    end
	
	return this
end