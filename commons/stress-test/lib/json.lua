local cjson = require "cjson"
local cjson2 = cjson.new()
local cjson_safe = require "cjson.safe"

local json = {}

function json.fromJson(t)
    return cjson.decode(t)
end

function json.toJson(t)
    return cjson.encode(t)
end

function json.loadFile(file)
    local data = {}
    local content
    local f=io.open(file,"r")
    if f~=nil then
        content = f:read("*all")
        io.close(f)
    else
        print("No data!")
        return lines
    end
    data = json.fromJson(content)
    return data
end

return json