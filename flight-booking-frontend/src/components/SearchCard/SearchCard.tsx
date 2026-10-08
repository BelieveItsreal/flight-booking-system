import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router'
import { ArrowLeftRight, ArrowUpRight, PlaneLanding, PlaneTakeoff } from 'lucide-react'
import CityField from './CityField.tsx'
import DatePickerField from './DatePickerField.tsx'
import TravellerClassField from './TravellerClassField.tsx'
import { useAppDispatch, useAppSelector } from '../../store/hooks.ts'
import {
    setDeparture,
    setFrom,
    setReturnDate,
    setTo,
    setTravellers,
    swapCities,
} from '../../store/searchSlice.ts'
import type { Airport } from '../../data/airports.ts'
import { fromISODate, toISODate } from '../../utils/date.ts'

interface SearchInput {
    from: Airport | null
    to: Airport | null
    departure: string | null
}

type SearchErrors = Partial<Record<keyof SearchInput, string>>

function validateSearch({ from, to, departure }: SearchInput): SearchErrors {
    const errors: SearchErrors = {}
    if (!from) errors.from = 'Please select a departure city'
    if (!to) errors.to = 'Please select a destination city'
    if (from && to && from.code === to.code) errors.to = 'From and To cannot be the same city'
    if (!departure) errors.departure = 'Please select a departure date'
    return errors
}

function SearchCard() {
    const dispatch = useAppDispatch()
    const navigate = useNavigate()

    // Read each value separately, so the card only re-renders when one of them changes
    const from = useAppSelector((state) => state.search.from)
    const to = useAppSelector((state) => state.search.to)
    const departure = useAppSelector((state) => state.search.departure)
    const returnDate = useAppSelector((state) => state.search.returnDate)
    const travellers = useAppSelector((state) => state.search.travellers)

    // Local UI state only
    const [submitted, setSubmitted] = useState(false)
    const [swapRotation, setSwapRotation] = useState(0)

    // Errors are calculated, and only shown after the first search attempt
    const errors = submitted ? validateSearch({ from, to, departure }) : {}

    const handleSwap = () => {
        dispatch(swapCities())
        setSwapRotation((rotation) => rotation + 180)
    }

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault()
        setSubmitted(true)

        // The null checks are already covered by validateSearch; repeated here so TypeScript narrows the types
        if (!from || !to || !departure || Object.keys(validateSearch({ from, to, departure })).length > 0) return

        const payload = { from: from.code, to: to.code, departure, returnDate, ...travellers }
        console.log('Search payload:', payload)

        const params = new URLSearchParams({
            from: from.code,
            to: to.code,
            depart: departure,
            adults: String(travellers.adults),
            children: String(travellers.children),
            infants: String(travellers.infants),
            class: travellers.cabinClass,
        })
        if (returnDate) params.set('return', returnDate)

        navigate(`/search?${params}`)
    }

    return (
        <form
            onSubmit={handleSubmit}
            noValidate
            className="w-full max-w-200 space-y-3 rounded-xl bg-surface p-5 shadow-float"
        >
            {/* Row 1: From ⇄ To */}
            <div className="grid grid-cols-[1fr_auto_1fr] items-start gap-3">
                <CityField
                    label="From"
                    icon={PlaneTakeoff}
                    value={from}
                    onSelect={(airport) => dispatch(setFrom(airport))}
                    error={errors.from}
                />

                <button
                    type="button"
                    onClick={handleSwap}
                    aria-label="Swap origin and destination"
                    className="mt-2.5 grid size-10 place-items-center rounded-full bg-primary text-white transition-colors hover:bg-primary-light"
                >
                    <ArrowLeftRight
                        className="size-4 transition-transform duration-300"
                        style={{ rotate: `${swapRotation}deg` }}
                    />
                </button>

                <CityField
                    label="To"
                    icon={PlaneLanding}
                    value={to}
                    onSelect={(airport) => dispatch(setTo(airport))}
                    error={errors.to}
                />
            </div>

            {/* Row 2: Departure | Return  +  Traveller & Class */}
            <div className="grid grid-cols-[3fr_2fr] items-start gap-3">
                <div>
                    <div
                        className={`grid grid-cols-2 divide-x divide-line rounded-lg border ${
                            errors.departure ? 'border-red-500' : 'border-line'
                        }`}
                    >
                        <DatePickerField
                            label="Departure"
                            selected={fromISODate(departure)}
                            onSelect={(date) => dispatch(setDeparture(toISODate(date)))}
                            className="rounded-l-lg"
                        />
                        <DatePickerField
                            label="Return"
                            placeholder="Add Return"
                            selected={fromISODate(returnDate)}
                            onSelect={(date) => dispatch(setReturnDate(toISODate(date)))}
                            minDate={fromISODate(departure)}
                            className="rounded-r-lg"
                        />
                    </div>
                    {errors.departure && <p className="mt-1 text-xs text-red-600">{errors.departure}</p>}
                </div>

                <TravellerClassField
                    value={travellers}
                    onChange={(value) => dispatch(setTravellers(value))}
                    className="rounded-lg border border-line"
                />
            </div>

            {/* Row 3: Search */}
            <button
                type="submit"
                className="flex w-full items-center justify-center gap-2 rounded-lg bg-accent py-3.5 text-sm font-semibold tracking-wide text-white uppercase transition-colors hover:bg-accent-dark"
            >
                Search Flights
                <ArrowUpRight className="size-4" />
            </button>
        </form>
    )
}

export default SearchCard
