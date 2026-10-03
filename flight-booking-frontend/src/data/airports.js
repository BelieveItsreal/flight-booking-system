export const AIRPORTS = [
    { code: 'DEL', city: 'Delhi', name: 'Indira Gandhi International Airport' },
    { code: 'BOM', city: 'Mumbai', name: 'Chhatrapati Shivaji Maharaj International Airport' },
    { code: 'BLR', city: 'Bengaluru', name: 'Kempegowda International Airport' },
    { code: 'MAA', city: 'Chennai', name: 'Chennai International Airport' },
    { code: 'CCU', city: 'Kolkata', name: 'Netaji Subhas Chandra Bose International Airport' },
    { code: 'HYD', city: 'Hyderabad', name: 'Rajiv Gandhi International Airport' },
    { code: 'GOI', city: 'Goa', name: 'Dabolim Airport' },
    { code: 'COK', city: 'Kochi', name: 'Cochin International Airport' },
    { code: 'AMD', city: 'Ahmedabad', name: 'Sardar Vallabhbhai Patel International Airport' },
    { code: 'PNQ', city: 'Pune', name: 'Pune Airport' },
    { code: 'JAI', city: 'Jaipur', name: 'Jaipur International Airport' },
    { code: 'LKO', city: 'Lucknow', name: 'Chaudhary Charan Singh International Airport' },
    { code: 'TRV', city: 'Thiruvananthapuram', name: 'Trivandrum International Airport' },
    { code: 'GAU', city: 'Guwahati', name: 'Lokpriya Gopinath Bordoloi International Airport' },
    { code: 'IXC', city: 'Chandigarh', name: 'Shaheed Bhagat Singh International Airport' },
    { code: 'SXR', city: 'Srinagar', name: 'Sheikh ul-Alam International Airport' },
    { code: 'DXB', city: 'Dubai', name: 'Dubai International Airport' },
]

export const findAirport = (code) => AIRPORTS.find((airport) => airport.code === code) ?? null

// { city: 'Delhi', code: 'DEL' } → 'Delhi (DEL)'
export const formatAirport = (airport) => (airport ? `${airport.city} (${airport.code})` : '')

// Matches city, code or airport name; empty query shows every airport
export function searchAirports(query) {
    const q = query.trim().toLowerCase()
    if (!q) return AIRPORTS

    return AIRPORTS.filter(
        (airport) =>
            airport.city.toLowerCase().includes(q) ||
            airport.code.toLowerCase().includes(q) ||
            airport.name.toLowerCase().includes(q),
    )
}
