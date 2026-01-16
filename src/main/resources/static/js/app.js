let currentFlights = [];
const exchangeRates = { "USD": 1, "EUR": 0.91, "INR": 83.0, "GBP": 0.78 };

function updateCount(type, delta) {
    const el = document.getElementById(type + 'Count');
    let count = parseInt(el.innerText);
    count = Math.max(type === 'adults' ? 1 : 0, count + delta);
    el.innerText = count;
}

async function performSearch() {
    const from = document.getElementById('from').value.toUpperCase();
    const to = document.getElementById('to').value.toUpperCase();
    const depDate = document.getElementById('depDate').value;
    const retDate = document.getElementById('retDate').value;

    // --- FIX: Grab both counts from the UI ---
    const adults = document.getElementById('adultsCount').innerText;
    const children = document.getElementById('childrenCount').innerText;

    if (from.length !== 3 || to.length !== 3) return alert("Please enter 3-letter IATA codes (e.g., JFK)");

    document.getElementById('loader').style.display = 'block';
    document.getElementById('results').innerHTML = '';

    try {
        // Now 'children' is defined and can be sent to the backend
        const url = `/api/flights/search?from=${from}&to=${to}&departureDate=${depDate}&returnDate=${retDate}&adults=${adults}&children=${children}`;
        const response = await fetch(url);
        currentFlights = await response.json();
        renderResults();
    } catch (e) {
        alert("Server is currently unreachable.");
    } finally {
        document.getElementById('loader').style.display = 'none';
    }
}

function renderResults() {
    const container = document.getElementById('results');
    const currency = document.getElementById('currencySelector').value;
    const rate = exchangeRates[currency];

    // Safety check for empty results
    if (currentFlights.length === 0) {
        container.innerHTML = `<div class="card" style="justify-content:center;">No flights found for these dates.</div>`;
        return;
    }

    const adults = parseInt(document.getElementById('adultsCount').innerText);
    const children = parseInt(document.getElementById('childrenCount').innerText);

    // Calculation logic: Children typically pay 70% of the fare
    const totalMultiplier = adults + (children * 0.7);

    container.innerHTML = currentFlights.map(f => {
        const total = (f.price * rate * totalMultiplier).toFixed(2);
        const isLow = f.status && f.status.includes("ONLY");

        return `
            <div class="card">
                <div>
                    <span class="status-badge ${isLow ? 'status-low' : 'status-available'}">${f.status}</span>
                    <br><strong>${f.airline}</strong><br><small>${f.flightNumber} • ${f.provider}</small>
                </div>
                <div style="text-align:center">
                    <div><strong>${f.origin} ✈️ ${f.destination}</strong></div>
                    <a href="${f.bookingUrl}" target="_blank" style="font-size:0.7rem; color:var(--ocean)">Destination Guide ℹ️</a>
                </div>
                <div style="text-align:right">
                    <div class="price-value">${getSymbol(currency)}${total}</div>
                    <button class="btn-main" onclick="window.open('https://www.amadeus.net', '_blank')">Book Now</button>
                </div>
            </div>
        `;
    }).join('');
}
async function fetchSuggestions(type) {
    const input = document.getElementById(type);
    const query = input.value;
    const list = document.getElementById(type + 'List');

    if (query.length < 2) return;

    try {
        const res = await fetch(`/api/airports/search?query=${query}`);
        const airports = await res.json();

        // Match the keys from your JSON: "IATA", "Airport name", "City"
        list.innerHTML = airports.map(a =>
            `<option value="${a.IATA}">${a['Airport name']} (${a.City}, ${a.Country})</option>`
        ).join('');
    } catch (e) {
        console.error("Prediction Error:", e);
    }
}
function getSymbol(c) { return {"USD":"$","EUR":"€","INR":"₹","GBP":"£"}[c]; }
function convertPrices() { if(currentFlights.length) renderResults(); }
function getSymbol(c) {
    return {"USD":"$","EUR":"€","INR":"₹","GBP":"£"}[c];
}

function convertPrices() {
    if (currentFlights.length > 0) {
        renderResults();
    }
}