local json = require "lib/json"

local data = {}

local function bodyUpdateMap(reservationId, customerId)
    local updates = {}

    local reservation = { name = "reservationId", value = reservationId }
    local order       = { name = "orderId",       value = reservationId }
    local invoice     = { name = "invoiceId",     value = reservationId }
    local customer    = { name = "customerId",    value = customerId    }

    updates["ecommerce.sales.CreateReservation"] =  { reservation, customer }
    updates["ecommerce.sales.ReserveProduct"]    =  { reservation }
    updates["ecommerce.sales.ConfirmReservation"] = { reservation }
    updates["ecommerce.invoicing.ReceivePayment"] = { order, invoice }

    local mt = {}
    mt.__index = function (table, key)
        return {}
    end
    setmetatable(updates, mt)
    return updates
end

function data.loadRequests(file)
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

function data.nextRequest(arg)
    local req, reservationId, customerId = arg.template, arg.reservationId, arg.customerId

    local ct = req.headers and req.headers["Command-Type"]
    for _, update in ipairs(bodyUpdateMap(reservationId, customerId)[ct]) do
        if update and update.name then
            req.body[update.name] = update.value
            req.bodyStr = json.toJson(req.body)
        end
    end

    if req.method == "GET" then
        req.path = req.path .. reservationId
    end

    return req
end

return data