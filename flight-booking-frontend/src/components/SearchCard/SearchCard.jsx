import { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router'
import { ArrowLeftRight, ArrowUpRight, PlaneLanding, PlaneTakeoff } from 'lucide-react'
import CityField from './CityField.jsx'
import DatePickerField from './DatePickerField.jsx'
import TravellerClassField from './TravellerClassField.jsx'
import {
    setDeparture,
    setFrom,
    setReturnDate,
    setTo,
    setTravellers,
    swapCities,
} from '../../store/searchSlice.js'
import { fromISODate, toISODate } from '../../utils/date.js'

function validateSearch({ from, to, departure }) {
    const errors = {}
    if (!from) errors.from = 'Please select a departure city'
    if (!to) errors.to = 'Please select a destination city'
    if (from && to && from.code === to.code) errors.to = 'From and To cannot be the same city'
    if (!departure) errors.departure = 'Please select a departure date'
    return errors
}

function SearchCard() {
    const dispatch = useDispatch()
    const navigate = useNavigate()

    // Read each value separately, so the card only re-renders when one of them changes
    const from = useSelector((state) => state.search.from)
    const to = useSelector((state) => state.search.to)
    const departure = useSelector((state) => state.search.departure)
    const returnDate = useSelector((state) => state.search.returnDate)
    const travellers = useSelector((state) => state.search.travellers)

    // Local UI state only
    const [submitted, setSubmitted] = useState(false)
    const [swapRotation, setSwapRotation] = useState(0)

    // Errors are calculated, and only shown after the first search attempt
    const errors = submitted ? validateSearch({ from, to, departure }) : {}

    const handleSwap = () => {
        dispatch(swapCities())
        setSwapRotation((rotation) => rotation + 180)
    }

    const handleSubmit = (event) => {
        event.preventDefault()
        setSubmitted(true)

        if (Object.keys(validateSearch({ from, to, departure })).length > 0) return

        const payload = { from: from.code, to: to.code, departure, returnDate, ...travellers }
        console.log('Search payload:', payload)

        const params = new URLSearchParams({
            from: from.code,
            to: to.code,
            depart: departure,
            adults: travellers.adults,
            children: travellers.children,
            infants: travellers.infants,
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
