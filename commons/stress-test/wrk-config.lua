local data = require("lib/data")
local uuid = require("uuid")

local threadsCounter = 0

function setup(thread)
    threadsCounter = threadsCounter + 1
    thread:set("customerId", threadsCounter)
end

function init(args)
    local dir = args[1] or error("stress-test directory not provided")
    requests = data.loadRequests(dir .. "/requests.json")
    step = 1
    local msg = "thread for customer %d created"
    print(msg:format(customerId))
end

function request()
    local req = data.nextRequest {
        template = requests[step],
        reservationId = uuid(),
        customerId = "customer-" .. customerId
    }
    wrk.port = req.port
    wrk.port = req.port

    local result = wrk.format(req.method, req.path, req.headers, req.bodyStr)
    print(result)

    return result
end

function response()
    if step == 6 then
        print("Stopping thread")
        wrk.thread:stop()
    else
        step = step + 1
    end
end

function delay()
    return 1000
end