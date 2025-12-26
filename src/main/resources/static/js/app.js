document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('flightSearchForm');
    const resultsContainer = document.getElementById('flightResults');

    const formatDateTime = (dateTimeString) => {
        const date = new Date(dateTimeString);
        return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });
    };

    const renderFlightCard = (flight) => {
        const expediaPrice = (flight.price * 1.05).toFixed(2);
        const tripDotComPrice = (flight.price * 0.98).toFixed(2);

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
                    <p style="font-size: 0.9em; font-weight: bold; margin-bottom: 5px;">📍 Best Rates Comparison:</p>
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
            </div>
        `;
    };

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const from = document.getElementById('from').value.toUpperCase();
        const to = document.getElementById('to').value.toUpperCase();

        resultsContainer.innerHTML = '<p class="doodle-text">Mapping your adventure... 🌍</p>';

        try {
            const response = await fetch(`/api/flights/search?from=${from}&to=${to}`);
            const flights = await response.json();
            resultsContainer.innerHTML = flights.length ? '' : '<p class="doodle-text">No flights found for this route.</p>';
            flights.forEach(f => resultsContainer.innerHTML += renderFlightCard(f));
        } catch (error) {
            resultsContainer.innerHTML = '<p class="doodle-text" style="color: red;">Error contacting the flight tower.</p>';
        }
    });
});