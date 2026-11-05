import type {CabinClass} from "../../store/searchSlice.ts";
import type {Fare} from "../../data/flights.ts";
import {formatINR} from "../../utils/currency.ts";

const LOW_SEATS = 5
const formatSeats = (seats: number) => `${seats} seat${seats === 1 ? '' : 's'} left`
interface FareRowProps{
    cabin: CabinClass
    fare: Fare
    flightNumber: string
    isSelected: boolean
    onSelect: () => void
    onBook: () => void
}
function FareRow({cabin, fare, flightNumber, isSelected, onSelect, onBook}: FareRowProps) {
    const isLow = fare.seatsLeft <= LOW_SEATS
    return(
        <div
            className={`relative flex flex-1 items-center justify-between gap-3 px-4 py-2.5 transition-colors ${
                isSelected ? 'bg-accent/10' : 'hover:bg-subtle'
            }`}
        >
        <button
            type="button"
            onClick={onSelect}
            aria-pressed={isSelected}
            className="min-w-0 text-left after:absolute after:inset-0"
        >
        <span className="block text-[13px] text-ink">{cabin}</span>
            {/* nowrap keeps "₹4,299 · 12 seats left" on one line */}
            <span className="block whitespace-nowrap">
                <span className="text-[15px] font-bold text-ink">{formatINR(fare.price)}</span>
                <span className="text-xs text-red-700"> · {formatSeats(fare.seatsLeft)}</span>
            </span>
        </button>
            {isSelected? (
                <button
                    type="button"
                    onClick={onBook}
                    aria-label={`Book ${cabin} on ${flightNumber}`}
                    className="relative z-10 shrink-0 rounded-md bg-accent px-4 py-2 text-[13px] font-semibold
                    tracking-wide text-white uppercase transition hover:bg-accent-dark active:scale-95"
                >
                    Book Now
                </button>
            ): isLow? (
                <span className="shrink-0 rounded-md bg-red-50 px-2 py-0.5 text-xs font-medium text-red-700 ring-1
                ring-red-200">
                    Only {fare.seatsLeft} left
                </span>
            ): null}
        </div>
    )
}
export default FareRow