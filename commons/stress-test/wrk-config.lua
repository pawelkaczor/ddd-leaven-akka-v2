local json = require("lib/json")
local data = require("lib/data")
local utils = require("lib/utils")
local socket = require("socket")

local threadGlobalCounter = 0

local function log(msg)
    print(msg)
end

function setup(thread)
    threadGlobalCounter = threadGlobalCounter + 1
    thread:set("threadCounter", threadGlobalCounter)
end

function init(args)
    local dir = args[1] or error("stress-test directory not provided")
    maxSessions = (args[2] and tonumber(args[2])) or 1000000000000
    requestTemplates = json.loadFile(dir .. "/requests.json")
    sessionCounter = 1
    step = 1
    uuid = require("uuid")
    math.randomseed(socket.gettime())
    uuid.randomseed(math.random()*100000000000)

    reservationId = uuid()
end

function delay()
    return tonumber(requestTemplates[step].delay)
end

function request()
    req = data.request {
        template      = requestTemplates[step],
        reservationId = reservationId,
        customerId    = "customer-" .. threadCounter
    }

    wrk.port = req.port
    wrk.port = req.port
    return wrk.format(req.method, req.path, req.headers, req.bodyStr)
end

function response(status, headers, body)
    utils.logResponse(status, headers, body, step, threadCounter)
    if step == 6 then
        sessionCounter = sessionCounter + 1
        if sessionCounter > maxSessions then
            wrk.thread:stop()
        else
            step = 1
            reservationId = uuid()
        end
    else
        step = step + 1
    end
end

function done(summary, latency, requests)
    utils.logThread(summary, latency, requests)
end

