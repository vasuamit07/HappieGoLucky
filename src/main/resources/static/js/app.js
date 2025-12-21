document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('flightSearchForm');
    const resultsContainer = document.getElementById('flightResults');

    const formatDateTime = (dateTimeString) => {
        const date = new Date(dateTimeString);
        return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });
    };

    const renderFlightCard = (flight) => {
        return `
            <div class="flight-card">
                <div class="card-item">
                    <strong>${flight.airline}</strong><br>
                    <small>${flight.flightNumber}</small>
                </div>
                <div class="card-item">
                    <span class="card-label">Depart:</span> ${formatDateTime(flight.departureTime)}
                </div>
                <div class="card-item">
                    <span class="card-label">Arrive:</span> ${formatDateTime(flight.arrivalTime)}
                </div>
                <div class="card-item price">$${flight.price}</div>
                <button onclick="bookFlight('${flight.id}')" class="buy-button">BOOK</button>
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