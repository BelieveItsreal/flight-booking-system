import type {Flight} from "../../data/flights.ts";
import type {CabinClass} from "../../store/searchSlice.ts";
import type {SortKey} from "../../utils/sortFlights.ts";
import {formatINR} from "../../utils/currency.ts";
import {formatDuration} from "../../utils/flight.ts";


interface SortTabsProps{
    flights: Flight[]
    cabin: CabinClass
    value: SortKey
    onChange: (key: SortKey) => void
}
function SortTabs({ flights, cabin, value, onChange }: SortTabsProps){
    const lowestPrice = Math.min(...flights.map((flight) => flight.fares[cabin].price))
    const shortestTrip = Math.min(...flights.map((flight) => flight.durationMinutes))
    const tabs: { key: SortKey; label: string }[] = [
        { key: 'cheapest', label: `Cheapest (${formatINR(lowestPrice)})` },
        { key: 'fastest', label: `Fastest (${formatDuration(shortestTrip)})` },
        { key: 'early', label: 'Early Departure' },
        { key: 'best', label: 'Best Value' },
    ]
    return(
        <div
            role="group"
            aria-label="Sort Flights"
            className="scrollbar-none flex gap-7 overflow-x-auto shadow-[inset_0_-1px_0_var(--color-line)]"
        >
            {tabs.map((tab) => {
                const isActive = tab.key === value
                return(
                    <button
                        key={tab.key}
                        type="button"
                        aria-pressed={isActive}
                        onClick={() => onChange(tab.key)}
                        className={`shrink-0 border-b-2 pt-1 pb-3 text-[15px] whitespace-nowrap transition-colors
                        focus-visible:-outline-offset-2 ${
                            isActive
                                ? 'border-primary font-semibold text-primary'
                                : 'border-transparent font-medium text-ink hover:text-primary'
                        }`}
                    >
                        {tab.label}
                    </button>
                )
            })}
        </div>
    )
}
export default SortTabs