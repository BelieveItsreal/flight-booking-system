import type {Flight} from "../data/flights.ts";
import type {CabinClass} from "../store/searchSlice.ts";
export type SortKey = 'cheapest' | 'fastest' | 'early' | 'best'

const RUPEES_PER_MINUTE = 20
const valueScore = (flight: Flight, cabin: CabinClass) =>
    flight.fares[cabin].price + flight.durationMinutes * RUPEES_PER_MINUTE

const COMPARE: Record<SortKey, (a: Flight, b: Flight, cabin: CabinClass) => number> = {
    cheapest: (a, b, cabin) => a.fares[cabin].price - b.fares[cabin].price,
    fastest: (a, b) => a.durationMinutes - b.durationMinutes,
    early: (a, b) => a.departureTime.localeCompare(b.departureTime),
    best: (a, b, cabin) => valueScore(a, cabin) - valueScore(b, cabin),
}

export function sortFlights(flights: Flight[], sortKey: SortKey, cabin: CabinClass): Flight[] {
    return flights.toSorted((a, b) => COMPARE[sortKey](a, b, cabin))
}