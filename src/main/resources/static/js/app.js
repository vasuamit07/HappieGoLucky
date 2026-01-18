let currentFlights = [];
const exchangeRates = { "USD": 1, "EUR": 0.91, "INR": 83.0, "GBP": 0.78 };

function updateCount(type, delta) {
    const el = document.getElementById(type + 'Count');
    let count = parseInt(el.innerText);
    count = Math.max(type === 'adults' ? 1 : 0, count + delta);
    el.innerText = count;
}

// --- NEW: Sorting Logic ---
function sortResults() {
    const criteria = document.getElementById('sortSelector').value;

    if (criteria === 'cheap') {
        currentFlights.sort((a, b) => a.price - b.price);
    } else if (criteria === 'fast') {
        // Simple sort by parsing duration string "14h 30m"
        currentFlights.sort((a, b) => parseDuration(a.duration) - parseDuration(b.duration));
    } else if (criteria === 'best') {
        // "Best" = Price + (DurationHours * $20 per hour value)
        currentFlights.sort((a, b) => {
            const scoreA = a.price + (parseDuration(a.duration) / 60 * 20);
            const scoreB = b.price + (parseDuration(b.duration) / 60 * 20);
            return scoreA - scoreB;
        });
    }
    renderResults();
}

// Helper to convert "14h 30m" into minutes for sorting
function parseDuration(dur) {
    let total = 0;
    if (dur.includes('h')) total += parseInt(dur.split('h')[0]) * 60;
    if (dur.includes('m')) total += parseInt(dur.split('m')[0]); // simplified
    return total;
}

async function performSearch() {
    const from = document.getElementById('from').value.toUpperCase();
    const to = document.getElementById('to').value.toUpperCase();
    const depDate = document.getElementById('depDate').value;
    const retDate = document.getElementById('retDate').value;
    const adults = document.getElementById('adultsCount').innerText;

    if (from.length !== 3 || to.length !== 3) return alert("Please enter 3-letter IATA codes");

    document.getElementById('loader').style.display = 'block';
    document.getElementById('results').innerHTML = '';

    try {
        const url = `/api/flights/search?from=${from}&to=${to}&departureDate=${depDate}&returnDate=${retDate}&adults=${adults}`;
        const response = await fetch(url);
        currentFlights = await response.json();

        // Default sort: Cheapest
        sortResults();

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

    if (currentFlights.length === 0) {
        container.innerHTML = `<div class="card" style="justify-content:center;">No flights found.</div>`;
        return;
    }

    const adults = parseInt(document.getElementById('adultsCount').innerText);
    const children = parseInt(document.getElementById('childrenCount').innerText);
    const totalMultiplier = adults + (children * 0.7);

    container.innerHTML = currentFlights.map(f => {
        const total = (f.price * rate * totalMultiplier).toFixed(2);
        const isLow = f.status && f.status.includes("ONLY");

        // --- NEW: Smart Booking Link (Google Flights fallback) ---
        // Generates a link to Google Flights for this route
        const googleLink = `https://www.google.com/travel/flights?q=Flights%20to%20${f.destination}%20from%20${f.origin}%20on%20${f.departureDate}`;

        // --- NEW: Stop Formatting ---
        let stopText = "";
        if (f.stopCount === 0) {
            stopText = `<span style="color:green; font-weight:bold;">Non-stop</span>`;
        } else {
            stopText = `<span style="color:#e67e22; font-weight:bold;">${f.stopCount} Stop(s)</span> <small>(${f.stopAirports.join(', ')})</small>`;
        }

        return `
            <div class="card">
                <div style="flex:1">
                    <span class="status-badge ${isLow ? 'status-low' : 'status-available'}">${f.status || 'AVAILABLE'}</span>
                    <br><strong>${f.airline}</strong><br><small>${f.flightNumber}</small>
                </div>

                <div style="flex:2; text-align:center">
                    <div style="font-size:1.2rem; font-weight:800; color:var(--ocean)">
                        ${f.origin} ➝ ${f.destination}
                    </div>
                    <div style="margin:5px 0;">
                        ⏱️ ${f.duration} &nbsp; • &nbsp; ${stopText}
                    </div>
                    <small>${f.departureDate} ${f.returnDate !== 'One-Way' ? ' - ' + f.returnDate : ''}</small>
                    <br>
                    <a href="${f.bookingUrl}" target="_blank" style="font-size:0.7rem; color:var(--text); text-decoration:underline;">Destination Guide ℹ️</a>
                </div>

                <div style="flex:1; text-align:right">
                    <div class="price-value">${getSymbol(currency)}${total}</div>
                    <button class="btn-main" style="padding: 10px 20px; margin-top:10px;"
                            onclick="window.open('${googleLink}', '_blank')">
                        Book Now ↗
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

function getSymbol(c) { return {"USD":"$","EUR":"€","INR":"₹","GBP":"£"}[c]; }
function convertPrices() { if(currentFlights.length) renderResults(); }
async function fetchSuggestions(type) {
    // (Keep your existing prediction code here)
    const input = document.getElementById(type);
    const query = input.value;
    const list = document.getElementById(type + 'List');
    if (query.length < 2) return;
    try {
        const res = await fetch(`/api/airports/search?query=${query}`);
        const airports = await res.json();
        list.innerHTML = airports.map(a =>
            `<option value="${a.IATA}">${a['Airport name']} (${a.City})</option>`
        ).join('');
    } catch (e) { console.error(e); }
}