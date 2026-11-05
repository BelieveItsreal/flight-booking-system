import type {CabinClass} from "../store/searchSlice.ts";
import { FLIGHT_SCHEDULE, type Flight } from '../data/flights.ts'
import {findAirport} from "../data/airports.ts";

export interface FlightServiceParams{
    from: string
    to: string
    departure: string
    adults: number
    children: number
    infants: number
    cabinClass: CabinClass
}

const FAKE_DELAY_MS = 600

export async function searchFlights(params: FlightServiceParams): Promise<Flight[]>{
    await new Promise((resolve) => setTimeout(resolve, FAKE_DELAY_MS))
    const { from, to, departure } = params
    if (!findAirport(from) || !findAirport(to) || from === to) return []
    return FLIGHT_SCHEDULE
        .filter((flight) => flight.via !== from && flight.via !== to)
        .map((flight) => ({
            ...flight,
            id: `${from}-${to}-${departure}-${flight.flightNumber}`,
            from,
            to,
        }))

}
