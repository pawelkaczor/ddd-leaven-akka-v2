/*
 Emit 1 tick per minute (assuming --stats-period-sec is not changed (default 30) or set to value lower than 60)
 */
fromStream('$stats-0.0.0.0:2113').
    when({
        '$statsCollected' : function(s,e) {
            var coeff = 1000 * 60; // 1 minute
            var cd = new Date(Math.round(new Date().getTime() / coeff) * coeff);
            if (s.lastDate == null || !(+(new Date(s.lastDate)) === +cd)) {
                s.lastDate = cd;
                emit('time', 'tick', cd, null);
            }
        }
    });