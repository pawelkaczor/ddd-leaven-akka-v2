local utils = {}

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
        print(recurse(args[1]))
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

function utils.logResponse(status, headers, body, step, threadId)
    print("------------------------------")
    print("Response ".. step .." with status: ".. status .." on thread ".. threadId)
    print("------------------------------")
    print("[response] Headers:")
    for key, value in pairs(headers) do
        print("[response]  - " .. key  .. ": " .. value)
    end
    print("[response] Body:")
    print(body)
end

function utils.logThread(summary, latency, requests)
    print("------------------------------")
    print("Requests")
    print("------------------------------")

    print(utils.typeof(requests))

    utils.var_dump(summary)
    utils.var_dump(requests)
end

return utils