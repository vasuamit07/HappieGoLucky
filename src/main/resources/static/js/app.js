document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('flightSearchForm');
    const resultsContainer = document.getElementById('flightResults');

    // Function to format the date and time string from the API
    const formatDateTime = (dateTimeString) => {
        const date = new Date(dateTimeString);
        return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });
    };

    // Function to render a single flight card
    const renderFlightCard = (flight) => {
        return `
            <div class="flight-card">
                <div class="card-item">
                    <span class="card-label">Airline:</span> <strong>${flight.airline}</strong>
                    (${flight.flightNumber})
                </div>
                <div class="card-item">
                    <span class="card-label">Depart:</span> ${formatDateTime(flight.departureTime)}
                </div>
                <div class="card-item">
                    <span class="card-label">Arrive:</span> ${formatDateTime(flight.arrivalTime)}
                </div>
                <div class="card-item price">$${flight.price.toFixed(2)}</div>
                <a href="#" class="buy-button">BOOK</a>
            </div>
        `;
    };

    // Handle form submission
    form.addEventListener('submit', async (e) => {
        e.preventDefault(); // Stop the default form submit behavior

        // Get values from the form inputs
        const from = document.getElementById('from').value.toUpperCase();
        const to = document.getElementById('to').value.toUpperCase();
        const date = document.getElementById('date').value; // YYYY-MM-DD

        resultsContainer.innerHTML = '<p class="initial-message doodle-text">Doodling up results...</p>';

        try {
            // CALL OUR SPRING BOOT API
            const response = await fetch(`/search?from=${from}&to=${to}&date=${date}`);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const flights = await response.json();

            // Clear loading message
            resultsContainer.innerHTML = '';

            if (flights.length === 0) {
                resultsContainer.innerHTML = '<p class="initial-message doodle-text">No flights found! Try JFK to LHR.</p>';
            } else {
                // Loop through all flights and add their HTML to the container
                flights.forEach(flight => {
                    resultsContainer.innerHTML += renderFlightCard(flight);
                });
            }

        } catch (error) {
            console.error("Fetch error:", error);
            resultsContainer.innerHTML = '<p class="initial-message doodle-text" style="color: red;">Error finding flights. Check the console.</p>';
        }
    });
});