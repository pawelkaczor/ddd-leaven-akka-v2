/*
 Emit 1 tick per minute (assuming --stats-period-sec is not changed (default 30) or set to value lower than 60)
 */
fromStream('$stats-0.0.0.0:2113').
    when({
        '$init' : function(s,e) {
            return { "timeMillis": new Date().getTime() };
        },
        '$statsCollected' : function(s,e) {
            var time = new Date();
            var timeMillis = time.getTime();
            if (s.timeMillis < timeMillis) {
                s.timeMillis = timeMillis;
                emit('clock', 'tick', { "time": time, "timeMillis": timeMillis});
            }
        }
    });