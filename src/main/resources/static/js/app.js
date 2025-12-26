document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('flightSearchForm');
    const resultsContainer = document.getElementById('flightResults');

    const formatDateTime = (dateTimeString) => {
        const date = new Date(dateTimeString);
        return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });
    };

    const renderFlightCard = (flight) => {
        // 1. We take our database price and create fake "competitor" prices
        const expediaPrice = (flight.price * 1.05).toFixed(2); // 5% more expensive
        const tripDotComPrice = (flight.price * 0.98).toFixed(2); // 2% cheaper (The "Best Rate")

        return `
            <div class="flight-card" style="flex-direction: column; align-items: stretch;">
                <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 2px dashed #2d3436; padding-bottom: 10px;">
                    <div>
                        <strong style="font-size: 1.4em;">${flight.airline}</strong>
                        <span style="background: #dfe6e9; padding: 2px 8px; border-radius: 5px; font-size: 0.8em;">${flight.flightNumber}</span><br>
                        <small>${flight.origin} → ${flight.destination}</small>
                    </div>
                    <div class="price" style="color: #00b894;">$${flight.price}</div>
                    <button onclick="bookFlight('${flight.id}')" class="buy-button">BOOK DIRECT</button>
                </div>

                <div style="margin-top: 15px;">
                    <p style="font-size: 0.9em; font-weight: bold; margin-bottom: 5px;">📍 Compare Best Rates:</p>
                    <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 10px;">

                        <div class="provider-box" style="border: 2px solid #2d3436; padding: 5px; border-radius: 8px; text-align: center; background: #fff;">
                            <small>Expedia</small><br>
                            <strong>$${expediaPrice}</strong><br>
                            <button onclick="window.open('https://www.expedia.com', '_blank')" style="font-size: 0.7em; cursor: pointer;">Visit</button>
                        </div>

                        <div class="provider-box" style="border: 3px solid #6c5ce7; padding: 5px; border-radius: 8px; text-align: center; background: #f1f2ff;">
                            <small>Trip.com</small> <span style="font-size: 0.6em; background: #fd79a8; color: white; padding: 1px 3px;">BEST</span><br>
                            <strong>$${tripDotComPrice}</strong><br>
                            <button onclick="window.open('https://www.trip.com', '_blank')" style="font-size: 0.7em; cursor: pointer;">Visit</button>
                        </div>

                        <div class="provider-box" style="border: 2px solid #2d3436; padding: 5px; border-radius: 8px; text-align: center; background: #fff;">
                            <small>HappieGoLucky</small><br>
                            <strong>$${flight.price}</strong><br>
                            <button onclick="bookFlight('${flight.id}')" style="font-size: 0.7em; cursor: pointer;">Select</button>
                        </div>

                    </div>
                </div>

                <div style="margin-top: 10px; font-size: 0.7em; color: #636e72;">
                    *Prices updated 2 minutes ago. Redirects to external site for checkout.
                </div>
            </div>
        `;
    };

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const from = document.getElementById('from').value.toUpperCase();
        const to = document.getElementById('to').value.toUpperCase();
        const date = document.getElementById('date').value;

        resultsContainer.innerHTML = '<p class="doodle-text">Doodling up results...</p>';

        try {
            const response = await fetch(`/search?from=${from}&to=${to}&date=${date}`);
            const flights = await response.json();
            resultsContainer.innerHTML = '';

            if (flights.length === 0) {
                resultsContainer.innerHTML = '<p class="doodle-text">No flights found! Try JFK to LHR.</p>';
            } else {
                flights.forEach(flight => {
                    resultsContainer.innerHTML += renderFlightCard(flight);
                });
            }
        } catch (error) {
            resultsContainer.innerHTML = '<p class="doodle-text" style="color: red;">Error finding flights.</p>';
        }
    });
});

window.bookFlight = async (flightId) => {
    try {
        const response = await fetch(`/api/bookings/${flightId}`, { method: 'POST' });

        if (response.status === 403 || response.status === 401) {
            alert("You need to login to book a flight!");
            window.location.href = "/login";
        } else if (response.ok) {
            alert("Success! Flight Doodled into your bookings.");
        } else {
            alert("Booking failed.");
        }
    } catch (error) {
        console.error("Error:", error);
    }
};