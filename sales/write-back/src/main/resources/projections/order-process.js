fromStreams(['$ce-Reservation', '$ce-Invoice']).
    when({
        'ecommerce.sales.ReservationConfirmed' : function(s,e) {
            linkTo('sales', e);
        },
        'ecommerce.invoicing.OrderBilled' : function(s,e) {
            linkTo('sales', e);
        },
        'ecommerce.invoicing.OrderBillingFailed' : function(s,e) {
            linkTo('sales', e);
        }
    });