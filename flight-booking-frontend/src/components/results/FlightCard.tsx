import { useState } from 'react'
import { AIRLINES } from '../../data/airlines.ts'
import type { Flight } from '../../data/flights.ts'
import type { CabinClass } from '../../store/searchSlice.ts'
import AirlineLogo from './AirlineLogo.tsx'
import FareRow from './FareRow.tsx'
import FlightRoute from './FlightRoute.tsx'

const CABINS: CabinClass[] = ['Economy', 'Business']

interface FlightCardProps {
    flight: Flight
    defaultCabin: CabinClass   // the class the user searched for; that row starts selected
    onBook: (flight: Flight, cabin: CabinClass) => void
}

function FlightCard({ flight, defaultCabin, onBook }: FlightCardProps) {
    const airline = AIRLINES[flight.airline]
    const [selectedCabin, setSelectedCabin] = useState<CabinClass>(defaultCabin)

    return (
        <article
            aria-label={`${airline.name} ${flight.flightNumber}`}
            className="flex flex-col overflow-hidden rounded-xl border border-line bg-surface shadow-card
            transition-shadow hover:shadow-float md:flex-row"
        >
            {/* Left part: airline + route */}
            <div className="flex flex-1 flex-col gap-4 p-4 sm:flex-row sm:items-center sm:gap-6 sm:px-5">
                <div className="flex w-32 shrink-0 items-center gap-3">
                    <AirlineLogo airline={airline} />
                    <div className="min-w-0">
                        <p className="truncate text-[15px] font-semibold text-ink">{airline.name}</p>
                        <p className="text-[13px] text-muted">{flight.flightNumber}</p>
                    </div>
                </div>

                <FlightRoute flight={flight} />
            </div>

            {/* Right part: one row per cabin class, split by a line */}
            <div className="border-t border-line md:w-72 md:shrink-0 md:border-t-0 md:border-l">
                <div className="flex h-full flex-col divide-y divide-line">
                    {CABINS.map((cabin) => (
                        <FareRow
                            key={cabin}
                            cabin={cabin}
                            fare={flight.fares[cabin]}
                            flightNumber={flight.flightNumber}
                            isSelected={cabin === selectedCabin}
                            onSelect={() => setSelectedCabin(cabin)}
                            onBook={() => onBook(flight, cabin)}
                        />
                    ))}
                </div>
            </div>
        </article>
    )
}

export default FlightCard
