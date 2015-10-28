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

return json