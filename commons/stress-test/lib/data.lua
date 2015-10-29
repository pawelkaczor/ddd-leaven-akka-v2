local json = require "lib/json"

local data = {}

local function bodyUpdates(reservationId, customerId)
    local updates = {}

    local reservation = { name = "reservationId", value = reservationId }
    local order       = { name = "orderId",       value = reservationId }
    local invoice     = { name = "invoiceId",     value = reservationId }
    local customer    = { name = "customerId",    value = customerId    }

    updates["ecommerce.sales.CreateReservation"] =  { reservation, customer }
    updates["ecommerce.sales.ReserveProduct"]    =  { reservation }
    updates["ecommerce.sales.ConfirmReservation"] = { reservation }
    updates["ecommerce.invoicing.ReceivePayment"] = { order, invoice }

    setmetatable(updates, {
        __index = function (table, key) return {} end
    })
    return updates
end

function data.request(arg)
    local req, reservationId, customerId = arg.template, arg.reservationId, arg.customerId

    local ct = req.headers and req.headers["Command-Type"]
    for _, update in ipairs(bodyUpdates(reservationId, customerId)[ct]) do
        if update and update.name then
            req.body[update.name] = update.value
            req.bodyStr = json.toJson(req.body)
        end
    end

    if req.method == "GET" then
        req.path = req.path .. reservationId
    end

    if req.method == "POST" then
        req.headers["Content-Type"] = "application/json"
    end

    return req
end

return data