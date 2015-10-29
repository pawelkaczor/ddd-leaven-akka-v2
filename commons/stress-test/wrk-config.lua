local json = require("lib/json")
local data = require("lib/data")
local utils = require("lib/utils")
local socket = require("socket")
local uuid = require("uuid")

local threadsCounter = 0

local function log(msg)
    print(msg)
end

local function newUuid()
    math.randomseed(socket.gettime())
    uuid.randomseed(math.random()*100000000000)
    return uuid()
end
--
-- wrk functions
--

function setup(thread)
    threadsCounter = threadsCounter + 1
    thread:set("customerId", threadsCounter)
end

function init(args)
    local dir = args[1] or error("stress-test directory not provided")
    maxSessions = (args[2] and tonumber(args[2])) or 1000000000000
    requestTemplates = json.loadFile(dir .. "/requests.json")
    sessionCounter = 1
    step = 1
    reservationId = newUuid()
end

function delay()
    return tonumber(requestTemplates[step].delay)
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

function response(status, headers, body)
    utils.logResponse(status, headers, body, step, customerId)
    if step == 6 then
        sessionCounter = sessionCounter + 1
        if sessionCounter > maxSessions then
            --log(string.format("Stopping customer: %d", customerId))
            wrk.thread:stop()
        else
            step = 1
            reservationId = newUuid()
        end
    else
        step = step + 1
    end
end

function done(summary, latency, requests)
    utils.logThread(summary, latency, requests)
end

