fromStreams(['$ce-Reservation', '$ce-Invoice', 'currentDeadlines-global']).
    when({
        'ecommerce.sales.ReservationConfirmed' : function(s,e) {
            linkTo('order', e);
        },
        'ecommerce.invoicing.OrderBilled' : function(s,e) {
            linkTo('order', e);
        },
        'ecommerce.invoicing.OrderBillingFailed' : function(s,e) {
            linkTo('order', e);
        },
        'ecommerce.invoicing.PaymentExpired' : function(s,e) {
            linkTo('order', e);
        }
    });