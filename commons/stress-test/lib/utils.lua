local utils = {}

local function log(msg)
    print(msg)
end

-- Resource: https://gist.github.com/lunixbochs/5b0bb27861a396ab7a86
local function string(o)
    return '"' .. tostring(o) .. '"'
end

local function recurse(o, indent)
    if indent == nil then indent = '' end
    local indent2 = indent .. '  '
    if type(o) == 'table' then
        local s = indent .. '{' .. '\n'
        local first = true
        for k,v in pairs(o) do
            if first == false then s = s .. ', \n' end
            if type(k) ~= 'number' then k = string(k) end
            s = s .. indent2 .. '[' .. k .. '] = ' .. recurse(v, indent2)
            first = false
        end
        return s .. '\n' .. indent .. '}'
    else
        return string(o)
    end
end

function utils.var_dump(...)
    local args = {...}
    if #args > 1 then
        var_dump(args)
    else
        log(recurse(args[1]))
    end
end

-- Resource: http://lua-users.org/wiki/TypeOf
function utils.typeof(var)
    local _type = type(var);
    if(_type ~= "table" and _type ~= "userdata") then
        return _type;
    end
    local _meta = getmetatable(var);
    if(_meta ~= nil and _meta._NAME ~= nil) then
        return _meta._NAME;
    else
        return _type;
    end
end

function utils.log_response(status, headers, body, step, threadId)
    log("------------------------------")
    log("Response ".. step .." with status: ".. status .." on thread ".. threadId)
    log("------------------------------")
---[[
    log("[response] Headers:")
    for key, value in pairs(headers) do
        log("[response]  - " .. key  .. ": " .. value)
    end
    log("[response] Body:")
    log(body)
--]]
end

function utils.log_summary(summary, latency, requests)
    log("------------------------------")
    log("Requests")
    log("------------------------------")

    log(utils.typeof(requests))

    utils.var_dump(summary)
    utils.var_dump(requests)
end

function utils.log(text)
    log(text)
end

return utils