local json = require("lib/json")
local data = require("lib/data")
local utils = require("lib/utils")
local socket = require("socket")

local threadGlobalCounter = 0

-- See: https://github.com/wg/wrk/blob/master/SCRIPTING

--[[
  The setup phase begins after the target IP address has been resolved and all
  threads have been initialized but not yet started.
]]

-- Called once for each thread
function setup(thread)
    threadGlobalCounter = threadGlobalCounter + 1
    thread:set("threadCounter", threadGlobalCounter)
end

--[[
   The running phase begins with a single call to init(), followed by
   a call to request() and response() for each request cycle.
]]

--[[
   Receives any extra command line arguments for the script which
   must be separated from wrk arguments with "--".
-- ]]
function init(args)
    requestTemplatesFile = args[1] or error("requests not provided")
    maxSessions = (args[2] and tonumber(args[2])) or 1000000000000
    utils.log("Loading requests templates from: " .. requestTemplatesFile)
    requestTemplates = json.loadFile(requestTemplatesFile)
    sessionCounter = 1
    stepsTotal = #requestTemplates
    step = 1
    uuid = require("uuid")
    math.randomseed(socket.gettime())
    uuid.randomseed(math.random()*100000000000)

    reservationId = uuid()
end

-- Returns the number of milliseconds to delay sending the next request.
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
    --utils.log_response(status, headers, body, step, threadCounter)
    if step == stepsTotal then
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
    --utils.log_summary(summary, latency, requests)
end

