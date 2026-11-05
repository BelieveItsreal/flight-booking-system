import { useId } from 'react'
import { Plane } from 'lucide-react'
import { findAirport } from '../../data/airports.ts'
import type { Flight } from '../../data/flights.ts'
import { formatDuration, formatStops } from '../../utils/flight.ts'

interface TimeBlockProps{
    label: string
    time: string
    airportCode: string
}

function TimeBlock({ label, time, airportCode }:TimeBlockProps) {
    const city = findAirport(airportCode)?.city ?? airportCode
    return(
        <div className="shrink-0">
            <p className="text-2xl leading-none font-bold tracking-tight text-ink sm:text-[28px]">
                <span className="sr-only">{label} </span>
                {time}
            </p>
            <p className="mt-1 text-[15px] text-ink">{city}</p>
        </div>
    )
}

function RouteArc(){
    const markerId = `arrow${useId().replace(/[^a-zA-Z0-9_-]/g, '')}`
    return (
        <div className="relative">
            <svg viewBox="0 0 200 40" aria-hidden="true" className="block w-full text-accent">
                <defs>
                    {/* The arrowhead: a small triangle that the browser places at the end of the path and turns to follow it */}
                    <marker
                        id={markerId}
                        viewBox="0 0 10 10"
                        refX="7"
                        refY="5"
                        markerWidth="5"
                        markerHeight="5"
                        orient="auto"
                    >
                        <path d="M 0 0 L 10 5 L 0 10 Z" fill="currentColor" />
                    </marker>
                </defs>
                {/* Start bottom-left (4,34), pulled up by the control point (100,-16), end bottom-right (188,34) */}
                <path
                    d="M 4 34 Q 100 -16 188 34"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    markerEnd={`url(#${markerId})`}
                />
            </svg>
            {/* The curve's highest point is at y = 9 of 40 → 22.5% from the top, in the middle */}
            <Plane
                aria-hidden="true"
                className="absolute top-[22.5%] left-1/2 size-5 -translate-x-1/2 -translate-y-1/2 rotate-45 fill-primary text-primary"
            />
        </div>
    )
}
interface FlightRouteProps{
    flight: Flight
}
function FlightRoute({ flight }: FlightRouteProps) {
    const stops = flight.via ? `${formatStops(flight.stops)} via ${flight.via}` : formatStops(flight.stops)
    return (
        <div className="flex flex-1 items-center gap-3 sm:gap-4">
            <TimeBlock label="Departs" time={flight.departureTime} airportCode={flight.from} />
            <div className="min-w-0 flex-1 sm:max-w-56">
                <RouteArc />
                <p className="mt-0.5 text-center text-[13px] text-ink">
                    {formatDuration(flight.durationMinutes)} · {stops}
                </p>
            </div>
            <TimeBlock label="Arrives" time={flight.arrivalTime} airportCode={flight.to} />
        </div>
    )
}

export default FlightRoute