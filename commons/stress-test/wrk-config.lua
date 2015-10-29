local json = require("lib/json")
local data = require("lib/data")
local uuid = require("uuid")

local threadsCounter = 0

local _ = uuid.randomseed(os.time()*10000)

local function log(msg)
    print(msg)
end

local function newSession()
    sessionCounter = (sessionCounter and sessionCounter + 1) or 1
    if maxSessions and (sessionCounter > maxSessions) then
        --log(string.format("Stopping customer: %d", customerId))
        wrk.thread:stop()
    else
        step = 1
        reservationId = uuid.new()
    end
end

function setup(thread)
    threadsCounter = threadsCounter + 1
    thread:set("customerId", threadsCounter)
end

function init(args)
    local dir = args[1] or error("stress-test directory not provided")
    maxSessions = args[2] and tonumber(args[2])

    requestTemplates = json.loadFile(dir .. "/requests.json")
    newSession()
end

function request()
    req = data.request {
        template      = requestTemplates[step],
        reservationId = reservationId,
        customerId    = "customer-" .. customerId
    }
    wrk.port = req.port
    wrk.port = req.port
    return wrk.format(req.method, req.path, req.headers, req.bodyStr)
end

function response()
    if step == 6 then
        newSession()
    else
        step = step + 1
    end
end

--[[
done = function(summary, latency, requests)
    log("------------------------------")
    local errNr = summary.errors.status
    log(string.format("Nr of erroneous responses: %d", errNr))
end
]]

function delay()
    return 500
end

